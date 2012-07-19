<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns="http://www.pbcore.org/PBCore/PBCoreNamespace.html">
    <xsl:variable name="prices" select=
            "document('gallup')/id"/>
   <xsl:template match="*">
       <xsl:value-of select="$prices"/>
   </xsl:template>

</xsl:stylesheet>