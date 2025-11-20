<%--
  Created by IntelliJ IDEA.
  User: SIBL18202
  Date: 07-02-2025
  Time: 15:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>API Response Viewer - ${workItem}</title>

  <!-- Include Bootstrap and jsoneditor CSS -->
  <link href="../assets/css/bootstrap5.min.css" rel="stylesheet">
  <link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet">
  <link href="../assets/css/jsoneditor.min.css" rel="stylesheet" type="text/css">

  <style>
    :root {
      /* Harmonize these colors for a cleaner, more professional palette */
      --primary-color: #0d6efd;      /* Bootstrap-like primary blue */
      --secondary-color: #6c757d;    /* Bootstrap-like secondary gray */
      --success-color: #198754;      /* Bootstrap-like success green */
      --background-light: #f8f9fa;   /* Light gray background */
      --background-dark: #212529;    /* Dark gray/black background */
      --text-light: #212529;         /* Dark text for light backgrounds */
      --text-dark: #f8f9fa;          /* Light text for dark backgrounds */
      --border-color: #dee2e6;       /* Subtle border color */
      --hover-color: #0b5ed7;        /* Slightly darker shade of primary blue */
      --light-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      --medium-shadow: 0 3px 5px rgba(0, 0, 0, 0.15);
    }

    body {
      font-family: 'Inter', system-ui, -apple-system, sans-serif;
      margin: 0;
      padding: 20px;
      background-color: var(--background-light);
      color: var(--text-light);
      transition: background-color 0.3s, color 0.3s;
    }

    body.dark-mode {
      background-color: var(--background-dark);
      color: var(--text-dark);
    }

    .container-fluid {
      max-width: 1400px;
      margin: 0 auto;
    }

    .metadata {
      background-color: #ffffff;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 24px;
      box-shadow: var(--light-shadow);
    }

    .dark-mode .metadata {
      background-color: #2c3035;
      box-shadow: var(--light-shadow);
    }

    .toolbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px;
      background-color: #ffffff;
      border-radius: 8px;
      margin-bottom: 20px;
      box-shadow: var(--light-shadow);
    }

    .dark-mode .toolbar {
      background-color: #2c3035;
      box-shadow: var(--light-shadow);
    }

    .btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      padding: 8px 16px;
      border-radius: 6px;
      font-weight: 500;
      border: 1px solid transparent;
      transition: background-color 0.2s ease, transform 0.2s ease;
      cursor: pointer;
    }

    .btn-primary {
      background-color: var(--primary-color);
      color: #fff;
    }

    .btn-primary:hover {
      background-color: var(--hover-color);
      transform: translateY(-1px);
    }

    .btn-secondary {
      background-color: var(--secondary-color);
      color: #fff;
    }

    .btn-secondary:hover {
      opacity: 0.9;
      transform: translateY(-1px);
    }

    .btn-info {
      background-color: #0dcaf0;
      color: #fff;
    }

    .btn-info:hover {
      filter: brightness(0.95);
      transform: translateY(-1px);
    }

    .btn-warning {
      background-color: #ffc107;
      color: #212529;
    }

    .btn-warning:hover {
      filter: brightness(0.95);
      transform: translateY(-1px);
    }

    .btn-danger {
      background-color: #dc3545;
      color: #fff;
    }

    .btn-danger:hover {
      filter: brightness(0.95);
      transform: translateY(-1px);
    }

    .nav-tabs {
      border-bottom: none;
      gap: 8px;
      margin-bottom: -1px; /* Make tab content flush below */
    }

    .nav-tabs .nav-link {
      border: none;
      border-radius: 6px 6px 0 0;
      padding: 10px 16px;
      font-weight: 500;
      color: var(--text-light);
      background-color: transparent;
      transition: background-color 0.2s ease;
    }

    .dark-mode .nav-tabs .nav-link {
      color: var(--text-dark);
    }

    .nav-tabs .nav-link.active {
      background-color: var(--primary-color);
      color: #fff !important;
    }

    .tab-content {
      background-color: #ffffff;
      border-radius: 0 8px 8px 8px;
      padding: 24px;
      box-shadow: var(--light-shadow);
    }

    .dark-mode .tab-content {
      background-color: #2c3035;
      box-shadow: var(--light-shadow);
    }

    .json-container {
      height: 600px;
      border: 1px solid var(--border-color);
      border-radius: 6px;
      overflow: hidden;
      background-color: #fff;
    }

    .dark-mode .json-container {
      background-color: #2c3035;
      border-color: #41474d;
    }

    .base64-content {
      background-color: #e9ecef;
      padding: 4px 8px;
      border-radius: 4px;
      font-family: monospace;
      display: inline-block;
      margin: 2px 0;
    }

    .dark-mode .base64-content {
      background-color: #3b4148;
    }

    .decode-button {
      display: inline-block;
      background-color: var(--primary-color);
      color: #fff;
      border: none;
      border-radius: 4px;
      padding: 4px 12px;
      cursor: pointer;
      font-size: 0.85em;
      margin-left: 8px;
      transition: background-color 0.2s ease, transform 0.2s ease;
    }

    .decode-button:hover {
      background-color: var(--hover-color);
      transform: translateY(-1px);
    }

    .form-check-input:checked {
      background-color: var(--primary-color);
      border-color: var(--primary-color);
    }
  </style>

</head>
<body>
<div class="container-fluid">
  <!-- Metadata Section -->
  <div class="metadata">
    <div class="row">
      <div class="col-md-6">
        <strong>Work Item:</strong> ${workItem}
      </div>
      <div class="col-md-6">
        <strong>API Endpoint:</strong> ${apiEndpoint}
      </div>
    </div>
  </div>

  <!-- Toolbar -->
  <div class="toolbar">
    <button class="btn btn-info" onclick="copyCurrentJson()">
      <i class="fas fa-clipboard"></i> Copy JSON
    </button>
    <button class="btn btn-danger" onclick="expandCurrent()"> <i class="fas fa-maximize"></i>  Expand All</button>
    <button class="btn btn-warning" onclick="collapseCurrent()"> <i class="fas fa-minimize"></i>  Collapse All</button>
    <div class="form-check form-check-inline float-end">
      <input class="form-check-input" type="checkbox" id="darkMode" onchange="toggleDarkMode()">
      <label class="form-check-label" for="darkMode">Dark Mode</label>
    </div>
  </div>

  <!-- Tabs -->
  <ul class="nav nav-tabs" id="jsonTabs" role="tablist">
    <li class="nav-item" role="presentation">
      <button class="nav-link active" id="request-tab" data-bs-toggle="tab"
              data-bs-target="#request" type="button" role="tab">Request</button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="response-tab" data-bs-toggle="tab"
              data-bs-target="#response" type="button" role="tab">Response</button>
    </li>
  </ul>

  <!-- Tab Content -->
  <div class="tab-content" id="jsonTabContent">
    <div class="tab-pane fade show active" id="request" role="tabpanel">
      <div id="requestEditor" class="json-container"></div>
    </div>
    <div class="tab-pane fade" id="response" role="tabpanel">
      <div id="responseEditor" class="json-container"></div>
    </div>
  </div>
</div>

<!-- Include Bootstrap, jsoneditor JS -->
<script src="../assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<script src="../assets/js/jsoneditor.min.js"></script>
<script>
  let requestEditor, responseEditor;

  function initializeEditors() {
    const editorOptions = {
      mode: 'tree',
      search: true,
      onEditable: () => false,
      limitDragging: true,
      onClassName: function(node) {
        if (isBase64String(node.value)) return 'base64-content';
      },
      onRenderValue: function(node, value) {
        if (isBase64String(value)) {
          let comp = encodeURIComponent(value);
          return `<span>${value.substring(0, 50)}... <button onclick="decodeBase64('${comp}')" class="decode-button">Decode</button></span>`;
        }
      }
    };

    requestEditor = new JSONEditor(document.getElementById('requestEditor'), editorOptions);
    responseEditor = new JSONEditor(document.getElementById('responseEditor'), editorOptions);

    const decodeAllButton = document.createElement('button');
    decodeAllButton.className = 'btn btn-danger ms-2';
    decodeAllButton.textContent = 'Decode All Base64';
    decodeAllButton.disabled = true;
    decodeAllButton.onclick = decodeAllBase64;
    document.querySelector('.toolbar').appendChild(decodeAllButton);

    try {
      const requestJson = ${requestJson};
      const responseJson = ${responseJson};
      requestEditor.set(requestJson);
      responseEditor.set(responseJson);
      checkForBase64();

      document.querySelectorAll('.nav-link').forEach(tab => {
        tab.addEventListener('click', () => {
          setTimeout(checkForBase64, 100);
        });
      });
    } catch (error) {
      console.error('Error parsing JSON:', error);
      alert('Error loading JSON data');
    }
  }

  function isBase64String(str) {
    return typeof str === 'string' &&
            /^[A-Za-z0-9+/=]+$/.test(str) &&
            str.length > 100;
  }

  function checkForBase64() {
    const editor = getCurrentEditor();
    let hasBase64 = false;

    try {
      const data = editor.get();
      const checkNode = (obj) => {
        if (!obj) return;
        if (typeof obj === 'string' && isBase64String(obj)) {
          hasBase64 = true;
          return;
        }
        if (typeof obj === 'object') {
          Object.values(obj).forEach(value => checkNode(value));
        }
      };

      checkNode(data);
      const decodeAllButton = document.querySelector('.toolbar .btn-primary:last-child');
      if (decodeAllButton) {
        decodeAllButton.disabled = !hasBase64;
      }
    } catch (error) {
      console.error('Error checking Base64:', error);
    }
  }

  async function decodeAllBase64() {
    const editor = getCurrentEditor();
    const data = editor.get();
    const base64Values = [];

    const findBase64 = (obj) => {
      if (!obj) return;
      if (typeof obj === 'string' && isBase64String(obj)) {
        base64Values.push(obj);
        return;
      }
      if (typeof obj === 'object') {
        Object.values(obj).forEach(value => findBase64(value));
      }
    };

    findBase64(data);
    for (const value of base64Values) {
      await decodeBase64(value);
    }
  }


  function getCurrentEditor() {
    return document.querySelector('#request.active') ? requestEditor : responseEditor;
  }

  function copyCurrentJson() {
    const editor = getCurrentEditor();
    const json = JSON.stringify(editor.get(), null, 2);
    navigator.clipboard.writeText(json)
            .then(() => {
              const btn = document.querySelector('.toolbar .btn-primary');
              const originalContent = btn.innerHTML;
              btn.textContent = 'Copied!';
              setTimeout(() => btn.innerHTML = originalContent, 2000);
            })
            .catch(err => console.error('Failed to copy:', err));
  }

  function expandCurrent() {
    getCurrentEditor().expandAll();
  }

  function collapseCurrent() {
    getCurrentEditor().collapseAll();
  }

  async function decodeBase64(encodedString) {
    try {
      const response = await fetch('../api/decode-base64', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          content: encodedString
        })
      });


      const result = await response.json();
      if (!result.success) {
        throw new Error(result.error || 'Failed to decode');
      }

      if (result.type.startsWith('image/') || result.type === 'application/pdf') {
        showBinaryModal(encodedString, {
          type: 'binary',
          mimeType: result.type,
          ext: result.type.split('/')[1]
        });
      } else {
        const decoded = atob(encodedString);
        showTextModal(decoded);
      }
    } catch (error) {
      console.error('Decode error:', error);
      alert("Error processing Base64 content: " + error.message);
    }
  }

  function showTextModal(decodedText) {
    const modal = new bootstrap.Modal(document.createElement('div'));
    let comp = encodeURIComponent(decodedText);
    modal.element.innerHTML = `
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Decoded Base64 Content</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <pre class="text-wrap">${decodedText}</pre>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" onclick="downloadText('${comp}')">
                                Download as Text
                            </button>
                        </div>
                    </div>
                </div>
            `;
    document.body.appendChild(modal.element);
    modal.show();
  }

  function showBinaryModal(base64Data, fileType) {
    const modalDiv = document.createElement('div');
    modalDiv.className = 'modal fade';
    modalDiv.setAttribute('tabindex', '-1');

    let previewHtml = fileType.mimeType.startsWith('image/')
            ? `<img src="data:${fileType.mimeType};base64,${base64Data}" class="img-fluid" alt="Image preview">`
            : `<div class="ratio ratio-16x9">
            <iframe src="data:${fileType.mimeType};base64,${base64Data}" allowfullscreen></iframe>
           </div>`;

    modalDiv.innerHTML = `
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Binary Content (${fileType.mimeType})</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    ${previewHtml}
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="downloadBinary('${base64Data}', '${fileType.mimeType}', 'file.${fileType.ext}')">
                        Download File
                    </button>
                </div>
            </div>
        </div>`;

    document.body.appendChild(modalDiv);
    const modal = new bootstrap.Modal(modalDiv);
    modal.show();
    modalDiv.addEventListener('hidden.bs.modal', () => {
      modal.dispose();
      modalDiv.remove();
    });
  }


  function downloadText(encodedText) {
    const text = decodeURIComponent(encodedText);
    const blob = new Blob([text], { type: 'text/plain' });
    downloadBlob(blob, 'decoded.txt');
  }

  function downloadBinary(base64Data, mimeType, filename) {
    const byteCharacters = atob(base64Data);
    const byteArrays = [];

    for (let offset = 0; offset < byteCharacters.length; offset += 512) {
      const slice = byteCharacters.slice(offset, offset + 512);
      const byteNumbers = new Array(slice.length);
      for (let i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i);
      }
      byteArrays.push(new Uint8Array(byteNumbers));
    }

    const blob = new Blob(byteArrays, { type: mimeType });
    downloadBlob(blob, filename);
  }

  function downloadBlob(blob, filename) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
  }

  function toggleDarkMode() {
    document.body.classList.toggle('dark-mode');
    const isDarkMode = document.body.classList.contains('dark-mode');
    
    const theme = isDarkMode ? 'darktheme' : 'default';
    requestEditor.setTheme(theme);
    responseEditor.setTheme(theme);
  }

  document.addEventListener('DOMContentLoaded', initializeEditors);
</script>
</body>
</html>
