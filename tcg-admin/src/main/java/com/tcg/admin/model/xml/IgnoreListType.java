//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.09 at 04:06:30 PM CST 
//


package com.tcg.admin.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IgnoreListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IgnoreListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="URIs" type="{}IgnoreURIs"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IgnoreListType", propOrder = {
    "urIs"
})
public class IgnoreListType {

    @XmlElement(name = "URIs", required = true)
    protected IgnoreURIs urIs;

    /**
     * Gets the value of the urIs property.
     * 
     * @return
     *     possible object is
     *     {@link IgnoreURIs }
     *     
     */
    public IgnoreURIs getURIs() {
        return urIs;
    }

    /**
     * Sets the value of the urIs property.
     * 
     * @param value
     *     allowed object is
     *     {@link IgnoreURIs }
     *     
     */
    public void setURIs(IgnoreURIs value) {
        this.urIs = value;
    }

}
