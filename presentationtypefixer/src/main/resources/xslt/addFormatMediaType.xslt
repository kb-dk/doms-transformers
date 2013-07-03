<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns2="http://www.pbcore.org/PBCore/PBCoreNamespace.html"
                xmlns:my="http://doms.statsbiblioteket.dk/digitv/mediatype/channelmapping" exclude-result-prefixes="my"
        >


    <xsl:output method="xml" indent="yes"/>

    <xsl:param name="channelMapping"/>
    <xsl:variable name="channelID" select="//ns2:pbcorePublisher[ns2:publisherRole='channel_name']/ns2:publisher"/>



    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ns2:formatLocation">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>

        <xsl:if test="document($channelMapping)/*/my:map/entry[@key=$channelID]">
            <ns2:formatMediaType>
                <xsl:value-of select="document($channelMapping)/*/my:map/entry[@key=$channelID]"/>
            </ns2:formatMediaType>
        </xsl:if>

    </xsl:template>


    <xsl:template match="ns2:formatMediaType">
    </xsl:template>
</xsl:stylesheet>