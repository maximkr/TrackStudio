function pasteHandler(e) {
    var cbData;
    if (e.clipboardData) {
        cbData = e.clipboardData;
    } else if (window.clipboardData) {
        cbData = window.clipboardData;
    }

    if (e.msConvertURL) {
        var fileList = cbData.files;
        if (fileList.length > 0) {
            for (var i = 0; i < fileList.length; i++) {
                var blob = fileList[i];
                console.log("Image blob: " + blob);
                readPastedBlob(blob);
            }
        }
    }
    if (cbData && cbData.items) {
        if ((text = cbData.getData("text/plain"))) {
            // Text pasting is already handled
            return;
        }
        for (var i = 0; i < cbData.items.length; i++) {
            if (cbData.items[i].type.indexOf('image') !== -1) {
                var blob = cbData.items[i].getAsFile();
                readPastedBlob(blob);
            }
        }
    }

    function readPastedBlob(blob) {
        if (blob) {
            reader = new FileReader();
            reader.onload = function(evt) {
                pasteImage(evt.target.result);
            };
            reader.readAsDataURL(blob);
        }
    }

    function pasteImage(source) {
        var image = "<img src='" + source + "' data-mce-selected='1'></img>";
        window.tinyMCE.execCommand('mceInsertContent', false, image);
    }
}

tinymce.init({
    browser_spellcheck : true,
    convert_urls: false,
    selector: '.mceEditor',
    statusbar: false,
    height: 150,
    // forced_root_block : 'div',
    // paste_as_text: true,
    paste_data_images: true,
    plugins: [
        'advlist autolink lists link image charmap print preview anchor',
        'searchreplace visualblocks code fullscreen',
        'insertdatetime media table contextmenu paste code',
        'textcolor template'
    ],
    toolbar: 'insertfile undo redo | styleselect fontselect fontsizeselect | forecolor backcolor | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | template',
    setup : function(editor)
    {
        editor.on('paste', pasteHandler);

        editor.on('init', function()
        {
            this.getDoc().body.style.fontSize = '14px';
        });

        editor.on('BeforeSetContent', function (e) {
            console.log(e.content);
            var node = e.content;
            if (node.indexOf('<span class="fancytree-expander">') !== -1) {
                var start = node.substring(0, node.indexOf('<span class="fancytree-expander">'));
                node = node.substring(node.indexOf('<span class="fancytree-expander">'), node.lenght);
                node = node.substring(node.indexOf('title="') + 7, node.lenght);
                node = start + '#' + node.substring(0, node.indexOf(' ')) + node.substring(node.indexOf('</span>') + 7, node.lenght);
            }
            e.content = node;
            console.log(e.content);
        });

        editor.addMenuItem('syntax_highlight', {
            text: 'Add code',
            context: 'tools',
            onclick: function() {
                editor.insertContent(
                    '[code=java][/code]'
                );
            }
        });

        editor.addMenuItem('video', {
            text: 'Add video',
            context: 'tools',
            onclick: function() {
                editor.insertContent(
                    'Video{task_number:attach_id}'
                );
            }
        });
    },
    templates : [
        {
            title : "--------"
        },
        {
            title : "FullPathLink",
            url : "template_macros/FullPathLink.html",
            description : "It converts tasks number to a link and replaces tasks name to full link"
        },
        {
            title : "SimpleNameLink",
            url : "template_macros/SimpleNameLink.html",
            description : "It converts tasks number to a link"
        },
        {
            title : "--------"
        },
        {
            title : "SubtaskFilter",
            url : "template_macros/SubtaskFilter.html",
            description : "It converts tasks number to a table which has list of subtasks by filter"
        },
        {
            title : "--------"
        },
        {
            title : "UDFViewMacro",
            url : "template_macros/UDFViewMacro.html",
            description : "It converts tasks number to a view of udf"
        },
        {
            title : "--------"
        },
        {
            title : "BurndownChart",
            url : "template_macros/BurndownChart.html",
            description : "It converts tasks number to a Burndown Chat"
        },
        {
            title : "PersonalActivityChart",
            url : "template_macros/PersonalActivityChart.html",
            description : "It converts tasks number to a PersonalActivity Chart"
        },
        {
            title : "TeamActivityChart",
            src : "template_macros/TeamActivityChart.html",
            description : "It converts tasks number to a TeamActivity Chart"
        },
        {
            title : "TrendChart",
            url : "template_macros/TrendChart.html",
            description : "It converts tasks number to a Trend Chart"
        },
        {
            title : "SpentTimeChart",
            url : "template_macros/SpentTimeChart.html",
            description : "It converts tasks number to a SpentTime Chart"
        },
        {
            title : "WorkloadChart",
            url : "template_macros/WorkloadChart.html",
            description : "It converts tasks number to a Workload Chart"
        },
        {
            title : "StateChart",
            url : "template_macros/StateChart.html",
            description : "It converts tasks number to a State Chart"
        },
        {
            title : "ResolutionChart",
            url : "template_macros/ResolutionChart.html",
            description : "It converts tasks number to a Resolution Chart"
        },
        {
            title : "SyntaxHighlighter",
            url : "template_macros/SyntaxHighlighter.html",
            description : "It highlights the code"
        }
    ]
});