package com.holyworld.sphereindicator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/**
 * Определяет тип сферы по NBT-лору предмета.
 *
 * Логика нечёткая: убираем § коды и JSON-обёртку перед сравнением,
 * ищем подстроки — чтобы пережить любое форматирование сервера.
 */
public enum SphereType {
    URON3_BRONYA2,   // Урон III + Броня II
    BRONYA3_URON2,   // Броня III + Урон II
    NONE;

    /**
     * Читает лор стека и возвращает тип сферы.
     * Вызывается только для minecraft:player_head.
     */
    public static SphereType detect(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return NONE;

        CompoundTag tag = stack.getTag();
        if (tag == null) return NONE;

        CompoundTag display = tag.getCompound("display");
        if (!display.contains("Lore")) return NONE;

        // TAG_String = 8
        ListTag loreList = display.getList("Lore", 8);
        if (loreList.isEmpty()) return NONE;

        // Ищем нужные комбинации построчно
        boolean uronIII   = containsSequence(loreList, "Урон",  "III");
        boolean bronyaIII = containsSequence(loreList, "Броня", "III");
        boolean uronII    = containsSequence(loreList, "Урон",  "II");
        boolean bronyaII  = containsSequence(loreList, "Броня", "II");

        if (uronIII && bronyaII)  return URON3_BRONYA2;
        if (bronyaIII && uronII)  return BRONYA3_URON2;

        return NONE;
    }

    /**
     * Возвращает true, если хотя бы одна строка лора содержит и подстроку a, и подстроку b.
     * (например "Урон" и "III" — в одной строке)
     */
    private static boolean containsSequence(ListTag loreList, String a, String b) {
        for (int i = 0; i < loreList.size(); i++) {
            String line = clean(loreList.getString(i));
            if (line.contains(a) && line.contains(b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Убирает JSON-обёртку и § форматирование.
     *
     * Лор в 1.16 может быть либо plain-строкой, либо JSON:
     *   {"text":"Урон III","italic":false,"color":"red"}
     * Мы извлекаем только поле "text".
     */
    private static String clean(String raw) {
        if (raw == null) return "";
        String s = extractJsonText(raw);
        // Убираем § + следующий символ (цветовые коды Minecraft)
        return s.replaceAll("§[0-9a-fk-orA-FK-OR]", "");
    }

    /**
     * Простой парсинг: ищет значение поля "text" в JSON-строке.
     * Если JSON не распознан — возвращает исходную строку.
     */
    private static String extractJsonText(String raw) {
        int idx = raw.indexOf("\"text\":");
        if (idx == -1) return raw;

        // Ищем первую кавычку после "text":
        int start = raw.indexOf('"', idx + 7);
        if (start == -1) return raw;

        // Ищем закрывающую кавычку (учитываем escaped \")
        int end = start + 1;
        while (end < raw.length()) {
            if (raw.charAt(end) == '"' && raw.charAt(end - 1) != '\\') break;
            end++;
        }
        if (end >= raw.length()) return raw;

        return raw.substring(start + 1, end);
    }
}
