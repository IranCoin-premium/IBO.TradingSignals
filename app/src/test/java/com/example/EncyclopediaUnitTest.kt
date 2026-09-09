package com.example

import com.example.data.model.EncyclopediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EncyclopediaUnitTest {

    @Test
    fun testSectionSixIntegrity() {
        val items = EncyclopediaRepository.sectionSixItems
        assertEquals("بخش ۶ تایی باید دقیقا دارای ۶ قانون باشد", 6, items.size)
        items.forEachIndexed { index, item ->
            assertEquals("شناسه بخش ۶ تایی باید معتبر باشد", "s6_${index + 1}", item.id)
            assertEquals("نوع بخش باید SECTION_6 باشد", "SECTION_6", item.sectionType)
            assertEquals("شماره ترتیبی باید دقیق باشد", index + 1, item.sectionNumber)
            assertFalse("عنوان نباید خالی باشد", item.title.isBlank())
            assertFalse("چکیده نباید خالی باشد", item.summary.isBlank())
            assertFalse("محتوای اصلی نباید خالی باشد", item.fullContent.isBlank())
        }
    }

    @Test
    fun testSectionThirtySixIntegrity() {
        val items = EncyclopediaRepository.sectionThirtySixItems
        assertEquals("بخش ۳۶ تایی باید دقیقا دارای ۳۶ الگوی کندل‌استیک باشد", 36, items.size)
        items.forEachIndexed { index, item ->
            assertEquals("شناسه بخش ۳۶ تایی باید معتبر باشد", "s36_${index + 1}", item.id)
            assertEquals("نوع بخش باید SECTION_36 باشد", "SECTION_36", item.sectionType)
            assertEquals("شماره ترتیبی باید دقیق باشد", index + 1, item.sectionNumber)
            assertFalse("عنوان الگو نباید خالی باشد", item.title.isBlank())
            assertFalse("توضیح الگو نباید خالی باشد", item.summary.isBlank())
            assertTrue(
                "جهت الگو باید CALL، PUT یا BOTH باشد",
                item.direction in listOf("CALL", "PUT", "BOTH", "NEUTRAL")
            )
        }
    }

    @Test
    fun testSectionSixtySevenIntegrity() {
        val items = EncyclopediaRepository.sectionSixtySevenItems
        assertEquals("بخش ۶۷ تایی باید دقیقا دارای ۶۷ اصطلاح تخصصی باشد", 67, items.size)
        items.forEachIndexed { index, item ->
            assertEquals("شناسه بخش ۶۷ تایی باید معتبر باشد", "s67_${index + 1}", item.id)
            assertEquals("نوع بخش باید SECTION_67 باشد", "SECTION_67", item.sectionType)
            assertEquals("شماره ترتیبی باید دقیق باشد", index + 1, item.sectionNumber)
            assertFalse("عنوان اصطلاح نباید خالی باشد", item.title.isBlank())
            assertFalse("شرح اصطلاح نباید خالی باشد", item.summary.isBlank())
            assertFalse("محتوا نباید خالی باشد", item.fullContent.isBlank())
        }
    }

    @Test
    fun testTotalItemsCountAndUniqueness() {
        val all = EncyclopediaRepository.allItems
        assertEquals("مجموع کل سرفصل‌های دانشنامه باید ۱۰۹ مورد باشد (۶ + ۳۶ + ۶۷)", 109, all.size)

        val uniqueIds = all.map { it.id }.toSet()
        assertEquals("تمام شناسه‌های سرفصل‌ها باید کاملاً یکتا باشند", 109, uniqueIds.size)
    }
}
