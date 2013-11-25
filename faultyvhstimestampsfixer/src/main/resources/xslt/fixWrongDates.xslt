<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns3="http://doms.statsbiblioteket.dk/types/vhs_metadata/0/1/#">

    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="filestarttime" select="substring-before(substring-after(substring-after(substring-after(substring-after(/ns3:vhs_metadata/ns3:filename, '_'), '_'), '_'), '_'), '_')"/>
    <xsl:variable name="filestoptime" select="substring-before(substring-after(substring-after(substring-after(substring-after(substring-after(/ns3:vhs_metadata/ns3:filename, '_'), '_'), '_'), '_'), '_'), '_')"/>
    <xsl:variable name="starttime" select="concat(substring($filestarttime, 1,4), '-', substring($filestarttime, 5,2), '-', substring($filestarttime, 7,2), 'T', substring($filestarttime, 9,2), ':', substring($filestarttime, 11,2), ':', substring($filestarttime, 13,2))"/>
    <xsl:variable name="stoptime" select="concat(substring($filestoptime, 1,4), '-', substring($filestoptime, 5,2), '-', substring($filestoptime, 7,2), 'T', substring($filestoptime, 9,2), ':', substring($filestoptime, 11,2), ':', substring($filestoptime, 13,2))"/>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ns3:vhs_label">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>

        <ns3:start_time><xsl:value-of select="$starttime"/></ns3:start_time>
        <ns3:stop_time><xsl:value-of select="$stoptime"/></ns3:stop_time>
    </xsl:template>


    <xsl:template match="ns3:start_time">
    </xsl:template>

    <xsl:template match="ns3:stop_time">
    </xsl:template>
</xsl:stylesheet>