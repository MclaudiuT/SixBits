document.addEventListener("DOMContentLoaded", function () {
  const folderInput = document.getElementById("folder-upload");
  const addFolderButton = document.getElementById("add-folder");
  const jobMatchButton = document.getElementById("job-match");
  const cvMatchButton = document.getElementById("cv-match");
  const slider1 = document.getElementById("slider1");
  const slider2 = document.getElementById("slider2");
  const slider3 = document.getElementById("slider3");
  const dragDropArea = document.getElementById("drag-drop-area");

  addFolderButton.addEventListener("click", function () {
    folderInput.click();
  });

  folderInput.addEventListener("change", function (event) {
    const files = event.target.files;
    if (files.length > 0) {
      alert(`You have selected ${files.length} files from the folder.`);
    }
  });

  jobMatchButton.addEventListener("click", function () {
    const value = slider1.value;
    alert(`Job match parameter set to ${value}`);
  });

  cvMatchButton.addEventListener("click", function () {
    const value = slider2.value;
    alert(`CV match parameter set to ${value}`);
  });

  slider1.addEventListener("input", function () {
    console.log(`Slider 1 value: ${slider1.value}`);
  });

  slider2.addEventListener("input", function () {
    console.log(`Slider 2 value: ${slider2.value}`);
  });

  slider3.addEventListener("input", function () {
    console.log(`Slider 3 value: ${slider3.value}`);
  });

  dragDropArea.addEventListener("dragover", (event) => {
    event.preventDefault();
    dragDropArea.classList.add("drag-over");
  });

  dragDropArea.addEventListener("dragleave", () => {
    dragDropArea.classList.remove("drag-over");
  });

  dragDropArea.addEventListener("drop", (event) => {
    event.preventDefault();
    dragDropArea.classList.remove("drag-over");

    const files = event.dataTransfer.files;
    console.log("Dropped files:", files);

    // Handle folder upload logic here
  });
});
