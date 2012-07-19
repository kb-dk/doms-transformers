<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns="http://www.pbcore.org/PBCore/PBCoreNamespace.html">

    <xsl:template match="ns:instantiationDate[@dateType='dateActualStart']">
        <xsl:copy>
            <xsl:copy-of select="./@*"/>
            <xsl:value-of select="document('tvmeter')/tvmeterProgram/startDate"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ns:instantiationDate[@dateType='dateActualEnd']">
        <xsl:copy>
            <xsl:copy-of select="./@*"/>
            <xsl:value-of select="document('tvmeter')/tvmeterProgram/endDate"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="replaceIf">
        <xsl:param name="tvmeterValue"/>
        <xsl:param name="exisitingValue"/>
        <xsl:choose>
            <xsl:when test="string-length($tvmeterValue) &gt; 0 and
            (string-length($exisitingValue)=0
            or $exisitingValue='0')">
                <xsl:copy>
                    <xsl:copy-of select="./@*"/>
                    <xsl:value-of select="$tvmeterValue"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template match="ns:instantiationDate[@dateType='created']">
        <xsl:call-template name="replaceIf">
            <xsl:with-param name="exisitingValue" select="normalize-space(text())"/>
            <xsl:with-param name="tvmeterValue" select="document('tvmeter')/tvmeterProgram/parsedProgramClassification/targetGroupProductionYear"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="ns:pbcoreTitle[@titleType='titel']">
        <xsl:call-template name="replaceIf">
            <xsl:with-param name="exisitingValue" select="normalize-space(text())"/>
            <xsl:with-param name="tvmeterValue" select="document('tvmeter')/tvmeterProgram/mainTitle"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="ns:pbcoreTitle[@titleType='originaltitel']">
        <xsl:call-template name="replaceIf">
            <xsl:with-param name="exisitingValue" select="normalize-space(text())"/>
            <xsl:with-param name="tvmeterValue" select="document('tvmeter')/tvmeterProgram/originalTitle"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="ns:extensionWrap[ns:extensionElement='episodenr']/ns:extensionValue">
        <xsl:call-template name="replaceIf">
            <xsl:with-param name="exisitingValue" select="normalize-space(text())"/>
            <xsl:with-param name="tvmeterValue" select="document('tvmeter')/tvmeterProgram/episodeNumber"/>
        </xsl:call-template>
    </xsl:template>



    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>