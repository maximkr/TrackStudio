<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>TrackStudio</title>
        <script type="text/javascript" src="<%=request.getContextPath()%>/html/jquery/jquery-1.11.2.min.js" ></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/html/jquery/jquery-ui.js" ></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/html/jquery/jquery.mousewheel.js" ></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/html/jquery/jquery.iviewer.js" ></script>
        <script type="text/javascript">
	        function getUrlParameter(sParam) {
		        var sPageURL = window.location.search.substring(1);
		        var sURLVariables = sPageURL.split('&');
		        for (var i = 0; i < sURLVariables.length; i++)
		        {
			        var sParameterName = sURLVariables[i].split('=');
			        if (sParameterName[0] == sParam)
			        {
				        return sParameterName[1];
			        }
		        }
	        }
	        var $ = jQuery;
            $(document).ready(function(){
                  var iv2 = $("#viewer2").iviewer({
                      src: getUrlParameter('uri')
                  });

                  $("#chimg").click(function()
                  {
                    iv2.iviewer('loadImage', "test_image.jpg");
                    return false;
                  });

                  var fill = false;
                  $("#fill").click(function()
                  {
                    fill = !fill;
                    iv2.iviewer('fill_container', fill);
                    return false;
                  });
            });
        </script>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/html/jquery/jquery.iviewer.css" />
        <style>
            .viewer {
                width: 99%;
                height: 100%;
                border: 1px solid black;
                position: relative;
            }
            .wrapper {
                overflow: hidden;
            }
        </style>
    </head>
    <body>
        <div class="wrapper">
            <div id="viewer2" class="viewer"></div>
        </div>
    </body>
</html>
