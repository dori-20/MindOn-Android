package com.example.mindon

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Ggmindmedinst")
data class GgResponse(
    @Element(name = "row")
    val rows: List<GgItem>
)

@Xml(name = "row")
data class GgItem(
    @PropertyElement(name = "SIGUN_CD") val sigunCode: String?,
    @PropertyElement(name = "SIGUN_NM") val sigunName: String?,

    // 기관명: 의료기관용(INST_NM) + 복지센터용(CENTER_NM)
    @PropertyElement(name = "INST_NM") val instName: String?,
    @PropertyElement(name = "CENTER_NM") val centerNameAlt: String?,

    // 전화번호: 의료기관용(TELNO_INFO) + 복지센터용(TELNO)
    @PropertyElement(name = "TELNO_INFO") val phoneInfo: String?,
    @PropertyElement(name = "TELNO") val phoneAlt: String?,

    @PropertyElement(name = "HOSPTL_DIV_NM") val divisionName: String?,
    @PropertyElement(name = "REFINE_LOTNO_ADDR") val lotAddress: String?,
    @PropertyElement(name = "REFINE_ROADNM_ADDR") val roadAddress: String?,
    @PropertyElement(name = "REFINE_ZIP_CD") val zipCode: String?,
    @PropertyElement(name = "REFINE_WGS84_LAT") val lat: String?,
    @PropertyElement(name = "REFINE_WGS84_LOGT") val lng: String?,
    @PropertyElement(name = "HMPG_URL") val homepage: String?,
    @PropertyElement(name = "RM") val etc: String?
) {
    val centerName: String
        get() = instName ?: centerNameAlt ?: "기관명 없음"

    val phone: String
        get() = phoneInfo ?: phoneAlt ?: "전화 없음"

    val centerId: String
        get() = "${centerName}_${roadAddress ?: lotAddress}_${phone}".hashCode().toString()
}