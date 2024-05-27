(function ($, $document) {
  $(document).on("click", ".generate-chatgpt-seo", function () {
    $(".generate-chatgpt-seo").html("Loading");
    $(".generate-chatgpt-seo").attr("disabled", "disabled");
    let myroot;
    let pageUrl =
      decodeURIComponent(
        window.location.href.replace(window.location.pathname + "?item=", "")
      ) + ".html?wcmmode=disabled";
    fetch(pageUrl)
      .then((response) => response.text())
      .then((html) => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");
        const scripts = doc.getElementsByTagName("script");
        myroot = doc.getElementsByClassName("root")[0].innerText;
        for (let i = 0; i < scripts.length; i++) {
          scripts[i].parentNode.removeChild(scripts[i]);
        }
        myroot = myroot
          .replace(/(<([^>]+)>)/gi, "")
          .replace(/[\n\r]+|[\s]{2,}/g, " ")
          .trim();
        let datasomething = { bodyContent: myroot };
        $.ajax({
          url: "/bin/chatgpt/seoexpertjob",
          type: "POST",
          data: datasomething,
          success: function (response) {
            console.log("response");
            console.log(response);
            try {
              let text = JSON.parse(response).choices[0].text;
              const titleMatches = text.match(/Title:(.*?)(\n|$)/);
              const descriptionMatches = text.match(/Description:(.*?)(\n|$)/);
              const keywordsMatches = text.match(/Keywords:(.*?)(\n|$)/);

              const title = titleMatches ? titleMatches[1].trim() : "";
              const description = descriptionMatches
                ? descriptionMatches[1].trim()
                : "";
              const keywords = keywordsMatches ? keywordsMatches[1].trim() : "";

              title && $(".seo-title").val(title);
              description && $(".seo-desc").val(description);
              keywords && $(".keyword-field").val(keywords);
            } catch {
              alert("Try Again. Server Busy.");
            }
          },
          complete: function () {
            $(".generate-chatgpt-seo").html("AutoGenerate SEO");
            $(".generate-chatgpt-seo").removeAttr("disabled", "disabled");
          },
          error: function () {
            alert("Request Failed!");
          },
        });
      })
      .catch((error) => console.error(error));
  });
})($, $(document));
