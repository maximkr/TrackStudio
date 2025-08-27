var dropbox;

function init() {
    dropbox = document.getElementById("dropbox");
    window.addEventListener("dragenter", dragenter, true);
    window.addEventListener("dragleave", dragleave, true);
    dropbox.addEventListener("dragover", dragover, false);
    dropbox.addEventListener("drop", drop, false);
}

function dragenter(e) {
    e.preventDefault();
    dropbox.setAttribute("dragenter", true);
}

function dragleave(e) {
    dropbox.removeAttribute("dragenter");
}

function dragover(e) {
    e.preventDefault();
}

function drop(e) {
    e.preventDefault();
    var dt = e.dataTransfer;
    var files = dt.files;
    dropbox.removeAttribute("dragenter");

    handleFiles(files);
}

function handleFiles(files) {
    for (var i = 0; i < files.length; i++) {
        var file = files[i];
        var reader = new FileReader();
        reader.onloadend = (function(aImg) { return function(e) { new FileUpload(aImg, e.target.result); }; })(file);
        reader.readAsBinaryString(file);
    }

}

function FileUpload(file, bin) {
    var xhr = new XMLHttpRequest();

    var self = this;
    xhr.upload.addEventListener("progress", function(e) {
        if (e.lengthComputable) {
            var percentage = Math.round((e.loaded * 100) / e.total);
            self.ctrl.update(percentage);
        }
    }, false);

    xhr.upload.addEventListener("load", function(e){
    }, false);

    xhr.open("POST", contextPath + "/upload?resourceId="+resourceId+"&taskUpload="+taskUpload+"&fileName="+encodeURIComponent(file.fileName));
    xhr.overrideMimeType('text/plain; charset=x-user-defined-binary');
    xhr.sendAsBinary(bin);
    if (taskUpload) {
        document.location.href = contextPath + "/task/" + number + "?thisframe=true";
    } else {
        document.location.href = contextPath + "/user/" + number + "?thisframe=true";
    }
}

function sendFiles() {
    var imgs = document.querySelectorAll(".obj");
    for (var i = 0; i < imgs.length; i++) {
        var reader = new FileReader();
        reader.onloadend = (function(aImg) { return function(e) { new FileUpload(aImg, e.target.result); }; })(imgs[i]);
        reader.readAsBinaryString(imgs[i].file);
    }

}

var name = navigator.userAgent;
if (name.toLowerCase().indexOf('firefox') != -1) {
    window.addEventListener("load", init, true);
}
