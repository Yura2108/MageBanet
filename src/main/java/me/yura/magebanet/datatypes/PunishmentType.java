package me.yura.magebanet.datatypes;

public enum PunishmentType {

    KICK(0, "кик"), MUTE(1, "мут"), BAN(2, "бан"), ADMIN_MUTE(3, "админ-мут"), ADMIN_BAN(4, "админ-бан");

    private final int weight;
    private final String localization;

    PunishmentType(int weight, String localization) {
        this.weight = weight;
        this.localization = localization;
    }

    public int getWeight() {
        return weight;
    }

    public static PunishmentType getPunishmentType(String input) {
        for(PunishmentType p : PunishmentType.values()) {
            if(p.toString().equalsIgnoreCase(input)) return p;
        }

        return null;
    }

    public String getLocalization() {
        return localization;
    }
}
