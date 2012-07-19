<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns="http://www.pbcore.org/PBCore/PBCoreNamespace.html">

    <xsl:strip-space elements="*"/>
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="/ns:PBCoreDescriptionDocument">
        <pbcoreDescriptionDocument xmlns="http://www.pbcore.org/PBCore/PBCoreNamespace.html"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <xsl:call-template name="assetDateStart"/>
            <xsl:call-template name="assetDateEnd"/>

            <xsl:apply-templates/>
        </pbcoreDescriptionDocument>
    </xsl:template>

    <xsl:template name="assetDateStart">
        <pbcoreAssetDate dateType="programScheduledStart">
            <xsl:value-of select="ns:pbcoreInstantiation/ns:pbcoreDateAvailable/ns:dateAvailableStart"/>
        </pbcoreAssetDate>
    </xsl:template>
    <xsl:template name="assetDateEnd" >
        <pbcoreAssetDate dateType="programScheduledEnd">
            <xsl:value-of select="ns:pbcoreInstantiation/ns:pbcoreDateAvailable/ns:dateAvailableStart"/>
        </pbcoreAssetDate>
    </xsl:template>


    <xsl:template match="ns:pbcoreIdentifier">
        <pbcoreIdentifier source="DigiTV"><xsl:value-of select="ns:identifier"/></pbcoreIdentifier>
    </xsl:template>
    <xsl:template match="ns:pbcoreTitle">
        <pbcoreTitle>
            <xsl:attribute name="titleType"><xsl:value-of select="ns:titleType"/></xsl:attribute>
            <xsl:value-of select="ns:title"/>
        </pbcoreTitle>
    </xsl:template>
    <xsl:template match="ns:pbcoreDescription">
        <pbcoreDescription>
            <xsl:attribute name="descriptionType"><xsl:value-of select="ns:descriptionType"/></xsl:attribute>
            <xsl:value-of select="ns:description"/>
        </pbcoreDescription>
    </xsl:template>
    <xsl:template match="ns:pbcoreGenre">
        <pbcoreGenre >
            <xsl:attribute name="annotation"><xsl:value-of select="substring-before(ns:genre,':')"/></xsl:attribute>
            <xsl:value-of select="substring-after(ns:genre,': ')"/>
        </pbcoreGenre>
    </xsl:template>
    <xsl:template match="ns:pbcoreCreator">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="ns:pbcoreContributor">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="ns:pbcorePublisher">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="ns:pbcoreInstantiation">
        <xsl:copy>
            <xsl:call-template name="instantiationIdentifier"/>
            <xsl:call-template name="dateCreated"/>
            <xsl:call-template name="dateIssued"/>
            <xsl:call-template name="dateBroadcastStart"/>
            <xsl:call-template name="dateBroadcastEnd"/>
            <xsl:call-template name="dimensions"/>
            <xsl:call-template name="standard"/>
            <xsl:call-template name="location"/>
            <xsl:call-template name="mediatype"/>
            <xsl:call-template name="duration"/>
            <xsl:call-template name="colors"/>
            <xsl:call-template name="channelconfig"/>
            <xsl:call-template name="annotations"/>

        </xsl:copy>
    </xsl:template>
    <xsl:template name="dateCreated">
        <instantiationDate dateType="created"><xsl:value-of select="ns:dateCreated"/></instantiationDate>
    </xsl:template>
    <xsl:template name="dateIssued">
        <instantiationDate dateType="issued"><xsl:value-of select="ns:dateIssued"/></instantiationDate>
    </xsl:template>
    <xsl:template name="location">
        <instantiationLocation><xsl:value-of select="ns:formatLocation"/></instantiationLocation>
    </xsl:template>
    <xsl:template name="mediatype">
        <instantiationMediaType><xsl:value-of select="ns:formatMediaType"/></instantiationMediaType>
    </xsl:template>
    <xsl:template name="standard">
        <instantiationStandard><xsl:value-of select="ns:formatStandard"/></instantiationStandard>
    </xsl:template>

    <xsl:template name="duration">
        <instantiationDuration><xsl:value-of select="ns:formatDuration"/></instantiationDuration>
    </xsl:template>
    <xsl:template name="dimensions">
        <instantiationDimensions unitsOfMeasure="aspectRatio"><xsl:value-of select="ns:formatAspectRatio"/></instantiationDimensions>
    </xsl:template>
    <xsl:template name="colors">
        <instantiationColors><xsl:value-of select="ns:formatColors"/></instantiationColors>
    </xsl:template>
    <xsl:template name="channelconfig">
        <instantiationChannelConfiguration><xsl:value-of select="ns:formatChannelConfiguration"/></instantiationChannelConfiguration>
    </xsl:template>
    <xsl:template name="dateBroadcastStart">
        <instantiationDate dateType="dateActualStart"><xsl:value-of select="ns:pbcoreDateAvailable/ns:dateAvailableStart"/></instantiationDate>
    </xsl:template>
    <xsl:template name="dateBroadcastEnd" >
        <instantiationDate dateType="dateActualEnd"><xsl:value-of select="ns:pbcoreDateAvailable/ns:dateAvailableEnd"/></instantiationDate>
    </xsl:template>
    <xsl:template name="instantiationIdentifier" >
        <instantiationIdentifier>
            <xsl:attribute name="source"><xsl:value-of select="ns:pbcoreFormatID/ns:formatIdentifierSource"/></xsl:attribute>
            <xsl:value-of select="ns:pbcoreFormatID/ns:formatIdentifier"/>
        </instantiationIdentifier>
    </xsl:template>
    <xsl:template name="annotations" >
        <xsl:for-each select="ns:pbcoreAnnotation/ns:annotation">
            <instantiationAnnotation><xsl:value-of select="."/></instantiationAnnotation>
        </xsl:for-each>
    </xsl:template>



    <xsl:template match="ns:pbcoreExtension">
        <pbcoreExtension>
            <extensionWrap>
                <xsl:apply-templates/>
            </extensionWrap>
        </pbcoreExtension>
    </xsl:template>

    <xsl:template match="ns:extension">
        <extensionElement>
            <xsl:value-of select="substring-before(.,':')"/>
        </extensionElement>
        <extensionValue>
            <xsl:value-of select="substring-after(.,':')"/>
        </extensionValue>

        <extensionAuthorityUsed>Ritzau</extensionAuthorityUsed>

    </xsl:template>

</xsl:stylesheet>
