<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <html>
            <head><title>Track Studio Report</title></head>
            <body>
                <table border="1">
                    <tr>
                        <th>Number</th>
                        <th>Name</th>
                    </tr>
                    <xsl:for-each select="trackstudio-task/task">
                        <tr>
                            <td><xsl:value-of select="number"/></td>
                            <td><xsl:value-of select="name"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body></html>
    </xsl:template>
</xsl:stylesheet>
