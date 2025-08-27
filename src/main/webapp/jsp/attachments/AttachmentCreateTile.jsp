<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<script type="text/javascript">
    var countFiles=0;
    function createNewForm() {
        var inn = document.getElementById("upload");

        var div = document.createElement("div");
        countFiles++;
        var fileinput = document.createElement("input");
        fileinput.type="file";
        fileinput.size=80;
        fileinput.name="file["+countFiles+"]";
        fileinput.onchange=createNewForm;

        var filedesc = document.createElement("input");
        filedesc.type="text";
        filedesc.size=80;
        filedesc.name="filedesc";



        div.appendChild(document.createTextNode("<I18n:message key="CHOOSE_FILE"/>"));
        div.appendChild(document.createElement("br"));
        div.appendChild(fileinput);
        div.appendChild(document.createElement("br"));
        div.appendChild(document.createTextNode("<I18n:message key="DESCRIPTION"/>"));
        div.appendChild(document.createElement("br"));
        div.appendChild(filedesc);
        div.appendChild(document.createElement("br"));
        inn.appendChild(div);
    }

    function createMultiForm(previoues) {
        countFiles++;
        showSelectedFiles(previoues);
        var inn = document.getElementById("upload");
        var div = document.createElement("div");
        var fileinput = document.createElement("input");
        fileinput.type="file";
        fileinput.size=80;
        fileinput.multiple='multiple';
        fileinput.name="file";
        fileinput.setAttribute('onchange', 'createMultiForm(this)');
        div.appendChild(document.createTextNode("<I18n:message key="CHOOSE_FILE"/>"));
        var span = document.createElement("span");
        span.id = 'span' + countFiles;

        var filedesc = document.createElement("input");
        filedesc.type="text";
        filedesc.size=80;
        filedesc.name="filedesc";

        div.appendChild(span);
        div.appendChild(document.createElement("br"));
        div.appendChild(fileinput);
        div.appendChild(document.createElement("br"));
        div.appendChild(document.createTextNode("<I18n:message key="DESCRIPTION"/>"));
        div.appendChild(document.createElement("br"));
        div.appendChild(filedesc);
        div.appendChild(document.createElement("br"));
        inn.appendChild(div);
    }

    function showSelectedFiles(el) {
        var files = el.files;
        var size = files.length;
        var html = '<I18n:message key="SELECTED"/> : ';
        for (var index=0;index!=size;++index) {
            html += '<span style="padding-left: 10px">' + files[index].name + '</span>';
        }
        var div = document.createElement("div");
        div.name = 'selects';
        div.innerHTML = html;
        var last = el.parentNode.childNodes[el.parentNode.childNodes.length-1];
        console.log(last.name);
        if (last.name == 'selects') {
            last.innerHTML = html;
        } else {
            el.parentNode.appendChild(div);
        }
    }

</script>
<table class="general" cellpadding="0" cellspacing="0">
    <c:if test="${hideHeader == null}">
        <caption>
            <I18n:message key="FILE_ADD"/>
        </caption>
    </c:if>
    <colgroup>
        <col class="col_1">
        <col class="col_2">
    </colgroup>
    <tr>
        <th>
            <I18n:message key="ATTACHMENT"/>
        </th>
        <td id="upload">
            <div>
                <I18n:message key="CHOOSE_FILE"/>
                <br>
	            <c:if test="${header['user-agent'] == 'MSIE'}">
		            <input type="file" id="file0" name="file[0]" size="80" onchange="createNewForm()"/>
	            </c:if>
	            <c:if test="${header['user-agent'] != 'MSIE'}">
		            <input type="file" multiple="multiple" name="file" onchange="createMultiForm(this);" />
	            </c:if>
                <br>
                <I18n:message key="DESCRIPTION"/>
                <br>
                <input type="text" size="80" name="filedesc"/>
            </div>
        </td>
    </tr>
</table>
