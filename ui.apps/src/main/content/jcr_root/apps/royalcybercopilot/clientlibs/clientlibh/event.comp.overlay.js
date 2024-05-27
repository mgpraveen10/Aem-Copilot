(function ($, channel, window, undefined) {
  "use strict";

  var ACTION_ICON =
    "/apps/royalcybercopilot/clientlibs/clientlibh/genai-icon.png";
  var ACTION_TITLE = "Generative AI";
  var ACTION_NAME = "Generation";

  let componentPath = "";
  let contentNodePath = "";
  const listToExclude = [
    "/container",
    "/responsivegrid",
    "/tabs",
    "/breadcrumb",
    "/accordion",
    "/contentfragment",
    "/button",
  ];
  var generationActionP = new Granite.author.ui.ToolbarAction({
    name: ACTION_NAME,
    icon: ACTION_ICON,
    text: ACTION_TITLE,
    execute: function (editable) {
      componentPath = "/apps/" + editable.type;

      contentNodePath = editable.path;

      $.ajax({
        url: "/bin/component",
        type: "GET",
        data: {
          componentPath: componentPath,
          contentPath: contentNodePath,
        },
        success: function (response) {
          try {
            setTargetFieldOption(response);
            $("#gen-ai-multi-item-node-select").hide();
            console.log();
            $("#genai-target-field-select").change(function () {
              changeDialogContent($(this).val());
              console.log();
              ShowMultiFieldItem($(this).val());
              console.log();
            });
          } catch (err) {
            console.log("Error in catch ", err);
          }
        },
      });
      $("#dynamic-on-demand").html("");
      showDialogs(contentNodePath);
      $("#promptvalue").val("");
      $("#genai-response").val("");
    },
    condition: function (editable) {
      return (
        editable &&
        editable.type.includes("/components/") &&
        !listToExclude.some((eachExclude) =>
          editable.type.includes(eachExclude)
        )
      );
    },
    isNonMulti: false,
  });

  channel.on("cq-layer-activated", function (event) {
    if (event.layer === "Edit") {
      Granite.author.EditorFrame.editableToolbar.registerAction(
        "Generation",
        generationActionP
      );
    }
  });

  function setTargetFieldOption(response) {
    let targetFieldOption = response.split(/\r?\n/);
    let dynamicDropTargetField = "";
    //onchange is working on selection of target field
    dynamicDropTargetField += `
    <label
  id="label_1e40fc68-900f-4110-98da-c9d1230895e7"
  class="coral-Form-fieldlabel"
  >Fields To Target</label
>
<coral-select
  class="coral-Form-field _coral-Dropdown"
  id="genai-target-field-select"
  name="./genai-target-field"
  labelledby="label_1e40fc68-900f-4110-98da-c9d1230895e7"
  data-foundation-validation
  data-validation
  __vent-id__="869"
  placeholder
  variant="default"
>
  <coral-select-item value>Choose Target Field</coral-select-item>
 
    `;
    targetFieldOption.forEach((element) => {
      if (element != "") {
        let jsonValue = JSON.parse(element);
        let JsonKeys = Object.keys(jsonValue)[0];

        dynamicDropTargetField +=
          `<coral-select-item value="` +
          JsonKeys +
          `">` +
          jsonValue[JsonKeys].split("_./")[0] +
          `</coral-select-item>`;
      }
    });
    dynamicDropTargetField += `
    <button
    is="coral-button"
    tracking="off"
    variant="_custom"
    iconposition="right"
    handle="button"
    type="button"
    aria-haspopup="listbox"
    id="coral-id-641"
    aria-controls="coral-id-642"
    aria-expanded="true"
    class="_coral-FieldButton _coral-Dropdown-trigger is-selected"
    aria-labelledby="label_1e40fc68-900f-4110-98da-c9d1230895e7 coral-id-643 "
    size="M"
  >
    <coral-icon
      icon="alert"
      role="img"
      hidden
      handle="invalidIcon"
      class="_coral-Dropdown-invalidIcon _coral-Icon _coral-Icon--sizeS"
      alt="invalid"
      aria-label="invalid"
      id="coral-id-644"
      size="S"
      aria-hidden="true"
    >
      <svg
        focusable="false"
        aria-hidden="true"
        class="_coral-Icon--svg _coral-Icon"
      >
        <use xlink:href="#spectrum-icon-18-Alert"></use>
      </svg>
    </coral-icon>
    <svg
      focusable="false"
      aria-hidden="true"
      class="_coral-Icon--svg _coral-Icon _coral-Dropdown-icon _coral-UIIcon-ChevronDownMedium"
    >
      <use xlink:href="#spectrum-css-icon-ChevronDownMedium"></use>
    </svg>
  </button>
</coral-select>
    `;
    $("#gen-ai-select").html(dynamicDropTargetField);
  }

  function setMultifieldNodeItem(response) {
    let targetFieldOption = response.split("\r\n");
    let dynamicDropTargetField = "";
    dynamicDropTargetField += `
    <label
  id="label_1e40fc68-900f-4110-98da-c9d1230895e7"
  class="coral-Form-fieldlabel"
  >Multifield Node To Target</label
>
<coral-select
  class="coral-Form-field _coral-Dropdown"
  id="genai-target-multi-field-select"
  name="./genai-target-field"
  labelledby="label_1e40fc68-900f-4110-98da-c9d1230895e7"
  data-foundation-validation
  data-validation
  __vent-id__="869"
  placeholder
  variant="default"
>
  <coral-select-item value="createNew">Add New</coral-select-item>
 
    `;
    targetFieldOption.forEach((element) => {
      if (element != "") {
        dynamicDropTargetField +=
          `<coral-select-item value="` +
          element +
          `">` +
          element +
          `</coral-select-item>`;
      }
    });
    dynamicDropTargetField += `
    <button
    is="coral-button"
    tracking="off"
    variant="_custom"
    iconposition="right"
    handle="button"
    type="button"
    aria-haspopup="listbox"
    id="coral-id-641"
    aria-controls="coral-id-642"
    aria-expanded="true"
    class="_coral-FieldButton _coral-Dropdown-trigger is-selected"
    aria-labelledby="label_1e40fc68-900f-4110-98da-c9d1230895e7 coral-id-643 "
    size="M"
  >
    <coral-icon
      icon="alert"
      role="img"
      hidden
      handle="invalidIcon"
      class="_coral-Dropdown-invalidIcon _coral-Icon _coral-Icon--sizeS"
      alt="invalid"
      aria-label="invalid"
      id="coral-id-644"
      size="S"
      aria-hidden="true"
    >
      <svg
        focusable="false"
        aria-hidden="true"
        class="_coral-Icon--svg _coral-Icon"
      >
        <use xlink:href="#spectrum-icon-18-Alert"></use>
      </svg>
    </coral-icon>
    <svg
      focusable="false"
      aria-hidden="true"
      class="_coral-Icon--svg _coral-Icon _coral-Dropdown-icon _coral-UIIcon-ChevronDownMedium"
    >
      <use xlink:href="#spectrum-css-icon-ChevronDownMedium"></use>
    </svg>
  </button>
</coral-select>
    `;
    $("#gen-ai-multi-item-node-select").html(dynamicDropTargetField);
  }

  function hideDialogs() {
    var dialog = document.querySelector("#GenerateAI");

    if (dialog) {
      dialog.hide();
      return;
    }
  }

  function ShowMultiFieldItem(value) {
    let contentMultiPath =
      contentNodePath + "/" + value.replace("_./img", "").split("_./")[1];

    if (value.replace("_./img", "").includes("_./")) {
      $.ajax({
        url: "/bin/multi/component/nodename",
        type: "GET",
        data: {
          contentPath: contentMultiPath,
        },
        success: function (response) {
          try {
            setMultifieldNodeItem(response);
            $("#gen-ai-multi-item-node-select").show();
          } catch {
            alert("Something went wrong while fetching multifield item");
          }
        },
      });
    } else {
      $("#gen-ai-multi-item-node-select").hide();
    }
  }

  function showDialogs(componentpath) {
    var dialog = document.querySelector("#GenerateAI");

    if (dialog) {
      var alert = $("#coralalert");
      alert[0].hide();
      dialog.content.querySelector("#coralwait").hide();
      dialog.footer.querySelector("#generate").disabled = false;
      dialog.show();
      return;
    }

    // Create the dialog
    var dialog = new Coral.Dialog().set({
      id: "GenerateAI",
      header: {
        innerHTML: "Generative AI",
      },
      content: {
        innerHTML: getDialogContentHTMLAI(),
      },
      footer: {
        innerHTML:
          '<button is="coral-button" id="generate"variant="primary">Generate</button>' +
          '<button is="coral-button" id="GenerationSave"variant="primary">Save</button>' +
          '<button is="coral-button" id="gen-ai-close-dialog" variant="primary" coral-close>Close</button>',
      },
      closable: true,
      movable: false,
      backdrop: "blur",
    });

    // Add an event listener to the submit button
    dialog.footer
      .querySelector("#GenerationSave")
      .addEventListener("click", function () {
        let targetField = dialog.content.querySelector(
          "#genai-target-field-select"
        ).value;
        let genResponse = "";
        let multiNodeItem = "";
        if (targetField.includes("_./img")) {
          var remoreImageURL = dialog.content.querySelector("#largeImage").src;
          var sizeDropdown = document.getElementById("imgSize");
          var sizeValue =
            sizeDropdown.options[sizeDropdown.selectedIndex].value;
          var data = {
            damfolderpath: "/content/dam/royalcybercopilot",
            imagename:
              promptvalue
                .toString()
                .replaceAll(/[\r\n]/g, "_")
                .toLowerCase() +
              "_" +
              sizeValue +
              "_" +
              new Date().valueOf() +
              "",
            operationname: "SaveImage",
            componentpath: componentpath,
            remoreImageURL: remoreImageURL,
          };

          var servletUrl = "/bin/imagegenerator";

          var xhr = new XMLHttpRequest();
          xhr.open("POST", servletUrl, true);
          xhr.setRequestHeader("Content-type", "application/json");
          dialog.content.querySelector("#coralwait").show();
          $("#coralalert")[0].hide();
          let damLocation = "";
          xhr.onreadystatechange = function () {
            damLocation = xhr.responseText;
            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
              dialog.content.querySelector("#coralwait").hide();
              $("#coralalert")[0].hide();
              dialog.hide();
              Granite.author.ContentFrame.reload();
            } else {
              dialog.content.querySelector("#coralwait").hide();
              var alert = $("#coralalert");
              alert[0].variant = "error";
              alert[0].header.textContent = "ERROR";
              alert[0].content.innerHTML = xhr.responseText;
              alert[0].show();
            }
          };
          xhr.send(JSON.stringify(data));

          //
          genResponse = data.damfolderpath + "/" + data.imagename + ".png";
        } else {
          genResponse = dialog.content.querySelector("#genai-response").value;
        }
        if ($("#gen-ai-multi-item-node-select").css("display") == "block") {
          multiNodeItem = dialog.content.querySelector(
            "#genai-target-multi-field-select"
          ).value;
        }

        if (
          $("#gen-ai-multi-item-node-select").css("display") == "block" &&
          multiNodeItem !== "createNew"
        ) {
          updateExitingMultiNode(genResponse, targetField, multiNodeItem);
        } else {
          saveNodeValue(genResponse, targetField);
          ShowMultiFieldItem($("#genai-target-field-select").val());
        }
      });

    // Add an event listener to the submit button
    dialog.footer
      .querySelector("#generate")
      .addEventListener("click", function () {
        dialog.footer.querySelector("#generate").disabled = true;
        var promptvalue = dialog.content.querySelector("#promptvalue").value;

        var targetField = dialog.content.querySelector(
          "#genai-target-field-select"
        ).value;
        if (targetField.includes("_./img")) {
          let imagePreview = dialog.content.querySelector("#imagePreview");
          let imagePreview1 = dialog.content.querySelector("#imagePreview1");
          let imagePreview2 = dialog.content.querySelector("#imagePreview2");
          dialog.footer.querySelector("#generate").disabled = true;
          var sizeDropdown = document.getElementById("imgSize");
          var sizeValue =
            sizeDropdown.options[sizeDropdown.selectedIndex].value;
          var data = {
            prompt: promptvalue,
            operationname: "Generate",
            sizevalue: sizeValue,
          };
          var servletUrl = "/bin/imagegenerator";

          var xhr = new XMLHttpRequest();
          xhr.open("POST", servletUrl, true);
          xhr.setRequestHeader("Content-type", "application/json");
          dialog.content.querySelector("#coralwait").show();
          $("#coralalert")[0].hide();

          dialog.footer.querySelector("#generate").disabled = false;
          xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
              console.log("xhr");
              console.log(xhr);
              console.log(xhr.responseText);
              let allImages = JSON.parse(xhr.responseText);
              console.log(allImages[0].url);
              console.log(allImages[1].url);
              console.log(allImages[2].url);
              imagePreview.src = allImages[0].url;
              imagePreview1.src = allImages[1].url;
              imagePreview2.src = allImages[2].url;
              dialog.content.querySelector("#coralwait").hide();

              var alert = $("#coralalert");
              alert[0].variant = "success";
              alert[0].header.textContent = "SUCCESS";
              alert[0].content.innerHTML = "Image Generated Successfully.";
              alert[0].show();
            } else {
              var alert = $("#coralalert");
              alert[0].variant = "error";
              alert[0].header.textContent = "ERROR";
              alert[0].content.innerHTML = xhr.responseText;
              alert[0].show();
              dialog.content.querySelector("#coralwait").hide();
            }
          };
          xhr.send(JSON.stringify(data));
        } else {
          var genResponse = dialog.content.querySelector("#genai-response");
          var selectedFeature = document.querySelector(
            '[name="GenAiFeatures"]:checked'
          ).value;

          let url = window.location.href;
          let urlParts = url.split("/");
          let language = ""; // Initialize with a default language if needed
          // Find the language in the URL
          for (let i = 0; i < urlParts.length; i++) {
            if (urlParts[i] === "content" && i + 3 < urlParts.length) {
              language = urlParts[i + 3]; // The language should be at this index
              break;
            }
          }

          const apiUrl = "/bin/GenAI";

          $.ajax({
            url: apiUrl,
            type: "POST",
            data: {
              promptvalue: promptvalue,
              lang: language,
              targetField: targetField,
              Feature: selectedFeature,
            },
            success: function (response) {
              console.log(response);
              let data = JSON.parse(response).choices[0].text.trim();
              console.log("data");
              console.log(data);
              if (selectedFeature == "ContentGeneration") {
                let mOptions = "<p>Please choose to save</p></br>";
                var resultOptions = data.split("\n");
                console.log("resultOptions");
                console.log(resultOptions);
                for (let i = 0; i < resultOptions.length; i++) {
                  console.log("ele");
                  resultOptions[i] = resultOptions[i].replace(/['"]+/g, "");
                  console.log(resultOptions[i]);
                  if (resultOptions[i].length > 0) {
                    if (resultOptions[i].search(/[0-3][.]/g) < 3)
                      resultOptions[i] = resultOptions[i].replace(
                        /[0-3][.]/g,
                        ""
                      );
                    mOptions +=
                      `<input
                      type="radio"
                      id="resultContent` +
                      i +
                      `"
                      name="responseOptions"
                      class="GenAi-input-btn"
                      value="` +
                      resultOptions[i] +
                      `"
                    />
                    <label class="GenAi-Radio-btn" for="resultContent` +
                      i +
                      `">` +
                      resultOptions[i] +
                      `</label><br>`;
                  }
                }
                console.log("mOptions");
                console.log(mOptions);
                $("#response-option").show();
                $("#response-option").html(mOptions);
              } else {
                $("#response-option").hide();
                genResponse.value = data.replaceAll(/(<([^>]+)>)/gi, "");
              }

              dialog.content.querySelector("#coralwait").hide();
              dialog.footer.querySelector("#generate").disabled = false;
              var alert = $("#coralalert");
              alert[0].variant = "success";
              alert[0].header.textContent = "SUCCESS";
              alert[0].content.innerHTML = "Please review and Save";
              alert[0].show();
            },
          });
        }

        // Send a request to the servlet

        dialog.content.querySelector("#coralwait").show();
        $("#coralalert")[0].hide();
      });

    $(document).on("click", 'input[name="responseOptions"]', function () {
      console.log($(this).val());
      $("#genai-response").val($(this).val());
    });

    dialog.on("coral-overlay:close", function (event) {
      hideDialogs();
    });
    // Open the dialog
    document.body.appendChild(dialog);
    dialog.show();
  }

  function getDialogContentHTMLAI() {
    return `<form class="coral-Form coral-Form--vertical">
    <section class="coral-Form-fieldset">
      <div class="coral-Form-fieldwrapper">
        <coral-wait hidden id="coralwait"></coral-wait>
        <coral-alert id="coralalert" style="min-width: 100%" hidden
          ><coral-alert-header></coral-alert-header
          ><coral-alert-content></coral-alert-content
        ></coral-alert>
      </div>
      <div id="gen-ai-select" class="coral-Form-fieldwrapper"></div>
      <div class="coral-Form-fieldwrapper">
        <textarea
          is="coral-textarea"
          class="coral-Form-field"
          placeholder="Enter your prompt"
          id="promptvalue"
        ></textarea>
      </div>
      <div
        id="gen-ai-multi-item-node-select"
        class="coral-Form-fieldwrapper"
      ></div>
      <div id="dynamic-on-demand">
      </div>
    </section>
  </form>
  `;
  }

  function saveNodeValue(genResponse, targetField) {
    var servletUrl = "/bin/updateProperty";

    if (targetField.includes("_./img")) {
      targetField = targetField.replace("_./img", "");
    }

    $.ajax({
      url: servletUrl,
      type: "GET",
      data: {
        targetNode: contentNodePath,
        propertyName: targetField,
        propertyValue: genResponse,
      },
      success: function (response) {
        try {
          Granite.author.ContentFrame.reload();
        } catch {}
      },
    });
  }

  function updateExitingMultiNode(genResponse, targetField, multiNodeItem) {
    let servletUrl = "/bin/updateMultiNode/ItemProperty";
    $.ajax({
      url: servletUrl,
      type: "GET",
      data: {
        targetNode: contentNodePath,
        propertyName: targetField,
        propertyValue: genResponse,
        targetMultiNodeName: multiNodeItem,
      },
      success: function (response) {
        try {
          Granite.author.ContentFrame.reload();
        } catch {}
      },
    });
  }

  function changeDialogContent(value) {
    let con = value.includes("_./img")
      ? `<div class="coral-Form-fieldwrapper">
      <div>
        <p>Please select image size:</p>
        <select id="imgSize" name="size">
          <option value="256x256">256x256</option>
          <option value="512x512">512x512</option>
          <option value="1024x1024">1024x1024</option>
        </select>
      </div>
    </div>
    <div id="copilot-img-con" class="coral-Form-fieldwrapper">    
      <div id="thumbs-resp">
        <img id="imagePreview" onclick="document.querySelector('#largeImage').setAttribute('src', this.getAttribute('src'))" class="copilot-img-thumb"/>
        <img id="imagePreview1" onclick="document.querySelector('#largeImage').setAttribute('src', this.getAttribute('src'))" class="copilot-img-thumb"/>
        <img id="imagePreview2" onclick="document.querySelector('#largeImage').setAttribute('src', this.getAttribute('src'))" class="copilot-img-thumb"/>
      </div>
      <div class="productImage">
        <img id="largeImage">
      </div>
    </div>`
      : `<div class="GenAi-RadioButton">
        <p>Please select</p>
        <input
          type="radio"
          id="ContentGeneration"
          name="GenAiFeatures"
          value="ContentGeneration"
          checked
        />
        <label class="GenAi-Radio" for="ContentGeneration">Generation</label>
        <input
          type="radio"
          id="TextCorrection"
          name="GenAiFeatures"
          value="TextCorrection"
        />
        <label class="GenAi-Radio" for="TextCorrection">Correction</label>
        <input
          type="radio"
          id="summarize"
          name="GenAiFeatures"
          value="summarize"
        />
        <label class="GenAi-Radio" for="summarize">Summarize</label>
      </div>
      <div id="response-option">
      </div>
      <div class="coral-Form-fieldwrapper">
        <textarea
          is="coral-textarea"
          class="coral-Form-field"
          placeholder="Gen AI Response"
          id="genai-response"
        ></textarea>
      </div>`;
    $("#dynamic-on-demand").html(con);
  }
})(jQuery, jQuery(document), this);
