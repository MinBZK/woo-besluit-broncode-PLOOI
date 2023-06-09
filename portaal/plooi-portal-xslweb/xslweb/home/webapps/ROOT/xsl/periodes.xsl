<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="#all"
                expand-text="yes"
                version="3.0">

  <xsl:variable name="periodes" as="element(periode)*">
    <periode>
      <id>vandaag</id>
      <label>Vandaag</label>
    </periode>
    <periode>
      <id>afgelopen-week</id>
      <label>Afgelopen week</label>
    </periode>
    <periode>
      <id>afgelopen-maand</id>
      <label>Afgelopen maand</label>
    </periode>
    <periode>
      <id>afgelopen-jaar</id>
      <label>Afgelopen jaar</label>
    </periode>
  </xsl:variable>

</xsl:stylesheet>