document.addEventListener("DOMContentLoaded", function () {
  const folderInput = document.getElementById("folder-input");
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

  folderInput.addEventListener("change", async function (event) {
    const files = event.target.files;
    if (files.length > 0) {
      alert(You have selected ${files.length} files.);
      // handle file upload logic here
    }
  });

  // Job match button fetch
  jobMatchButton.addEventListener("click", async () => {
    try {
      const cvId = 1; // Example CV id, get this dynamically if needed
      const response = await fetch(http://localhost:8080/job-matches/${cvId});
      const matches = await response.json();
      console.log("Job Matches:", matches);
      populateResultsTable(matches); // populate dynamically in table
    } catch (error) {
      console.error("Failed to fetch job matches:", error);
    }
  });

  // CV match button fetch
  cvMatchButton.addEventListener("click", async () => {
    try {
      const cvId = 1; // Example CV id
      const response = await fetch(http://localhost:8080/cv-matches/${cvId});
      const matches = await response.json();
      console.log("CV Matches:", matches);
      populateResultsTable(matches);
    } catch (error) {
      console.error("Failed to fetch CV matches:", error);
    }
  });

  // Slider values
  slider1.addEventListener("input", () => console.log(Slider 1 value: ${slider1.value}));
  slider2.addEventListener("input", () => console.log(Slider 2 value: ${slider2.value}));
  slider3.addEventListener("input", () => console.log(Slider 3 value: ${slider3.value}));

  // Drag & Drop area
  dragDropArea.addEventListener("dragover", (event) => {
    event.preventDefault();
    dragDropArea.classList.add("drag-over");
  });

  dragDropArea.addEventListener("dragleave", () => {
    dragDropArea.classList.remove("drag-over");
  });

  dragDropArea.addEventListener("drop", async (event) => {
    event.preventDefault();
    dragDropArea.classList.remove("drag-over");

    const files = event.dataTransfer.files;
    console.log("Dropped files:", files);

    const formData = new FormData();
    for (const file of files) {
      formData.append("file", file);
    }

    try {
      const response = await fetch("http://localhost:8080/upload-cv", {
        method: "POST",
        body: formData,
      });

      const data = await response.text();
      alert(Uploaded file successfully! Response: ${data});
    } catch (error) {
      console.error("Upload failed:", error);
      alert("Failed to upload CV");
    }
  });

  // Populate results table
  function populateResultsTable(matches) {
    const tableBody = document.querySelector("#results-table tbody");
    tableBody.innerHTML = ""; // Clear existing results

    matches.forEach(match => {
      const row = document.createElement("tr");

      const jobTitleCell = document.createElement("td");
      jobTitleCell.textContent = match.jobTitle;

      const companyCell = document.createElement("td");
      companyCell.textContent = match.companyName;

      const rationaleCell = document.createElement("td");
      rationaleCell.textContent = match.rationale;

      const scoreCell = document.createElement("td");
      scoreCell.textContent = match.matchScore;

      row.appendChild(jobTitleCell);
      row.appendChild(companyCell);
      row.appendChild(rationaleCell);
      row.appendChild(scoreCell);

      tableBody.appendChild(row);
    });
  }
});