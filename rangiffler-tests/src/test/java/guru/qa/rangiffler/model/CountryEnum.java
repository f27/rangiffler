package guru.qa.rangiffler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Random;

@AllArgsConstructor
public enum CountryEnum {
    AFGHANISTAN("Afghanistan", "af"),
    ALBANIA("Albania", "al"),
    ALGERIA("Algeria", "dz"),
    AMERICAN_SAMOA("American Samoa", "as"),
    ANDORRA("Andorra", "ad"),
    ANGOLA("Angola", "ao"),
    ANGUILLA("Anguilla", "ai"),
    ANTARCTICA("Antarctica", "aq"),
    ANTIGUA_AND_BARBUDA("Antigua and Barbuda", "ag"),
    ARGENTINA("Argentina", "ar"),
    ARMENIA("Armenia", "am"),
    ARUBA("Aruba", "aw"),
    AUSTRALIA("Australia", "au"),
    AUSTRIA("Austria", "at"),
    AZERBAIJAN("Azerbaijan", "az"),
    BAHAMAS("Bahamas", "bs"),
    BAHRAIN("Bahrain", "bh"),
    BANGLADESH("Bangladesh", "bd"),
    BARBADOS("Barbados", "bb"),
    BELARUS("Belarus", "by"),
    BELGIUM("Belgium", "be"),
    BELIZE("Belize", "bz"),
    BENIN("Benin", "bj"),
    BERMUDA("Bermuda", "bm"),
    BHUTAN("Bhutan", "bt"),
    BOLIVIA("Bolivia", "bo"),
    BOSNIA_AND_HERZEGOVINA("Bosnia and Herzegovina", "ba"),
    BOTSWANA("Botswana", "bw"),
    BOUVET_ISLAND("Bouvet Island", "bv"),
    BRAZIL("Brazil", "br"),
    BRITISH_INDIAN_OCEAN_TERRITORY("British Indian Ocean Territory", "io"),
    BRUNEI_DARUSSALAM("Brunei Darussalam", "bn"),
    BULGARIA("Bulgaria", "bg"),
    BURKINA_FASO("Burkina Faso", "bf"),
    BURUNDI("Burundi", "bi"),
    CAMBODIA("Cambodia", "kh"),
    CAMEROON("Cameroon", "cm"),
    CANADA("Canada", "ca"),
    CAPE_VERDE("Cape Verde", "cv"),
    CAYMAN_ISLANDS("Cayman Islands", "ky"),
    CENTRAL_AFRICAN_REPUBLIC("Central African Republic", "cf"),
    CHAD("Chad", "td"),
    CHILE("Chile", "cl"),
    CHINA("China", "cn"),
    CHRISTMAS_ISLAND("Christmas Island", "cx"),
    COCOS_KEELING_ISLANDS("Cocos (Keeling) Islands", "cc"),
    COLOMBIA("Colombia", "co"),
    COMOROS("Comoros", "km"),
    CONGO("Congo", "cg"),
    CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE("Congo, the Democratic Republic of the", "cd"),
    COOK_ISLANDS("Cook Islands", "ck"),
    COSTA_RICA("Costa Rica", "cr"),
    COTE_D_IVOIRE("Cote D`Ivoire", "ci"),
    CROATIA("Croatia", "hr"),
    CUBA("Cuba", "cu"),
    CYPRUS("Cyprus", "cy"),
    CZECH_REPUBLIC("Czech Republic", "cz"),
    DENMARK("Denmark", "dk"),
    DJIBOUTI("Djibouti", "dj"),
    DOMINICA("Dominica", "dm"),
    DOMINICAN_REPUBLIC("Dominican Republic", "do"),
    ECUADOR("Ecuador", "ec"),
    EGYPT("Egypt", "eg"),
    EL_SALVADOR("El Salvador", "sv"),
    EQUATORIAL_GUINEA("Equatorial Guinea", "gq"),
    ERITREA("Eritrea", "er"),
    ESTONIA("Estonia", "ee"),
    ETHIOPIA("Ethiopia", "et"),
    FALKLAND_ISLANDS_MALVINAS("Falkland Islands (Malvinas)", "fk"),
    FAROE_ISLANDS("Faroe Islands", "fo"),
    FIJI("Fiji", "fj"),
    FINLAND("Finland", "fi"),
    FRANCE("France", "fr"),
    FRENCH_GUIANA("French Guiana", "gf"),
    FRENCH_POLYNESIA("French Polynesia", "pf"),
    FRENCH_SOUTHERN_TERRITORIES("French Southern Territories", "tf"),
    GABON("Gabon", "ga"),
    GAMBIA("Gambia", "gm"),
    GEORGIA("Georgia", "ge"),
    GERMANY("Germany", "de"),
    GHANA("Ghana", "gh"),
    GIBRALTAR("Gibraltar", "gi"),
    GREECE("Greece", "gr"),
    GREENLAND("Greenland", "gl"),
    GRENADA("Grenada", "gd"),
    GUADELOUPE("Guadeloupe", "gp"),
    GUAM("Guam", "gu"),
    GUATEMALA("Guatemala", "gt"),
    GUINEA("Guinea", "gn"),
    GUINEA_BISSAU("Guinea-Bissau", "gw"),
    GUYANA("Guyana", "gy"),
    HAITI("Haiti", "ht"),
    HEARD_ISLAND_AND_MCDONALD_ISLANDS("Heard Island and Mcdonald Islands", "hm"),
    HOLY_SEE_VATICAN_CITY_STATE("Holy See (Vatican City State)", "va"),
    HONDURAS("Honduras", "hn"),
    HONG_KONG("Hong Kong", "hk"),
    HUNGARY("Hungary", "hu"),
    ICELAND("Iceland", "is"),
    INDIA("India", "in"),
    INDONESIA("Indonesia", "id"),
    IRAN_ISLAMIC_REPUBLIC_OF("Iran, Islamic Republic of", "ir"),
    IRAQ("Iraq", "iq"),
    IRELAND("Ireland", "ie"),
    ISRAEL("Israel", "il"),
    ITALY("Italy", "it"),
    JAMAICA("Jamaica", "jm"),
    JAPAN("Japan", "jp"),
    JORDAN("Jordan", "jo"),
    KAZAKHSTAN("Kazakhstan", "kz"),
    KENYA("Kenya", "ke"),
    KIRIBATI("Kiribati", "ki"),
    KOREA_DEMOCRATIC_PEOPLE_S_REPUBLIC_OF("Korea, Democratic People`s Republic of", "kp"),
    KOREA_REPUBLIC_OF("Korea, Republic of", "kr"),
    KUWAIT("Kuwait", "kw"),
    KYRGYZSTAN("Kyrgyzstan", "kg"),
    LAO_PEOPLE_S_DEMOCRATIC_REPUBLIC("Lao People`s Democratic Republic", "la"),
    LATVIA("Latvia", "lv"),
    LEBANON("Lebanon", "lb"),
    LESOTHO("Lesotho", "ls"),
    LIBERIA("Liberia", "lr"),
    LIBYAN_ARAB_JAMAHIRIYA("Libyan Arab Jamahiriya", "ly"),
    LIECHTENSTEIN("Liechtenstein", "li"),
    LITHUANIA("Lithuania", "lt"),
    LUXEMBOURG("Luxembourg", "lu"),
    MACAO("Macao", "mo"),
    MACEDONIA_THE_FORMER_YUGOSLAV_REPUBLIC_OF("Macedonia, the Former Yugoslav Republic of", "mk"),
    MADAGASCAR("Madagascar", "mg"),
    MALAWI("Malawi", "mw"),
    MALAYSIA("Malaysia", "my"),
    MALDIVES("Maldives", "mv"),
    MALI("Mali", "ml"),
    MALTA("Malta", "mt"),
    MARSHALL_ISLANDS("Marshall Islands", "mh"),
    MARTINIQUE("Martinique", "mq"),
    MAURITANIA("Mauritania", "mr"),
    MAURITIUS("Mauritius", "mu"),
    MAYOTTE("Mayotte", "yt"),
    MEXICO("Mexico", "mx"),
    MICRONESIA_FEDERATED_STATES_OF("Micronesia, Federated States of", "fm"),
    MOLDOVA_REPUBLIC_OF("Moldova, Republic of", "md"),
    MONACO("Monaco", "mc"),
    MONGOLIA("Mongolia", "mn"),
    MONTSERRAT("Montserrat", "ms"),
    MOROCCO("Morocco", "ma"),
    MOZAMBIQUE("Mozambique", "mz"),
    MYANMAR("Myanmar", "mm"),
    NAMIBIA("Namibia", "na"),
    NAURU("Nauru", "nr"),
    NEPAL("Nepal", "np"),
    NETHERLANDS("Netherlands", "nl"),
    NETHERLANDS_ANTILLES("Netherlands Antilles", "an"),
    NEW_CALEDONIA("New Caledonia", "nc"),
    NEW_ZEALAND("New Zealand", "nz"),
    NICARAGUA("Nicaragua", "ni"),
    NIGER("Niger", "ne"),
    NIGERIA("Nigeria", "ng"),
    NIUE("Niue", "nu"),
    NORFOLK_ISLAND("Norfolk Island", "nf"),
    NORTHERN_MARIANA_ISLANDS("Northern Mariana Islands", "mp"),
    NORWAY("Norway", "no"),
    OMAN("Oman", "om"),
    PAKISTAN("Pakistan", "pk"),
    PALAU("Palau", "pw"),
    PALESTINIAN_TERRITORY_OCCUPIED("Palestinian Territory, Occupied", "ps"),
    PANAMA("Panama", "pa"),
    PAPUA_NEW_GUINEA("Papua New Guinea", "pg"),
    PARAGUAY("Paraguay", "py"),
    PERU("Peru", "pe"),
    PHILIPPINES("Philippines", "ph"),
    PITCAIRN("Pitcairn", "pn"),
    POLAND("Poland", "pl"),
    PORTUGAL("Portugal", "pt"),
    PUERTO_RICO("Puerto Rico", "pr"),
    QATAR("Qatar", "qa"),
    REUNION("Reunion", "re"),
    ROMANIA("Romania", "ro"),
    RUSSIAN_FEDERATION("Russian Federation", "ru"),
    RWANDA("Rwanda", "rw"),
    SAINT_HELENA("Saint Helena", "sh"),
    SAINT_KITTS_AND_NEVIS("Saint Kitts and Nevis", "kn"),
    SAINT_LUCIA("Saint Lucia", "lc"),
    SAINT_PIERRE_AND_MIQUELON("Saint Pierre and Miquelon", "pm"),
    SAINT_VINCENT_AND_THE_GRENADINES("Saint Vincent and the Grenadines", "vc"),
    SAMOA("Samoa", "ws"),
    SAN_MARINO("San Marino", "sm"),
    SAO_TOME_AND_PRINCIPE("Sao Tome and Principe", "st"),
    SAUDI_ARABIA("Saudi Arabia", "sa"),
    SENEGAL("Senegal", "sn"),
    SEYCHELLES("Seychelles", "sc"),
    SIERRA_LEONE("Sierra Leone", "sl"),
    SINGAPORE("Singapore", "sg"),
    SLOVAKIA("Slovakia", "sk"),
    SLOVENIA("Slovenia", "si"),
    SOLOMON_ISLANDS("Solomon Islands", "sb"),
    SOMALIA("Somalia", "so"),
    SOUTH_AFRICA("South Africa", "za"),
    SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("South Georgia and the South Sandwich Islands", "gs"),
    SPAIN("Spain", "es"),
    SRI_LANKA("Sri Lanka", "lk"),
    SUDAN("Sudan", "sd"),
    SURINAME("Suriname", "sr"),
    SVALBARD_AND_JAN_MAYEN("Svalbard and Jan Mayen", "sj"),
    SWAZILAND("Swaziland", "sz"),
    SWEDEN("Sweden", "se"),
    SWITZERLAND("Switzerland", "ch"),
    SYRIAN_ARAB_REPUBLIC("Syrian Arab Republic", "sy"),
    TAIWAN_PROVINCE_OF_CHINA("Taiwan, Province of China", "tw"),
    TAJIKISTAN("Tajikistan", "tj"),
    TANZANIA_UNITED_REPUBLIC_OF("Tanzania, United Republic of", "tz"),
    THAILAND("Thailand", "th"),
    TIMOR_LESTE("Timor-Leste", "tl"),
    TOGO("Togo", "tg"),
    TOKELAU("Tokelau", "tk"),
    TONGA("Tonga", "to"),
    TRINIDAD_AND_TOBAGO("Trinidad and Tobago", "tt"),
    TUNISIA("Tunisia", "tn"),
    TURKEY("Turkey", "tr"),
    TURKMENISTAN("Turkmenistan", "tm"),
    TURKS_AND_CAICOS_ISLANDS("Turks and Caicos Islands", "tc"),
    TUVALU("Tuvalu", "tv"),
    UGANDA("Uganda", "ug"),
    UKRAINE("Ukraine", "ua"),
    UNITED_ARAB_EMIRATES("United Arab Emirates", "ae"),
    UNITED_KINGDOM("United Kingdom", "gb"),
    UNITED_STATES("United States", "us"),
    UNITED_STATES_MINOR_OUTLYING_ISLANDS("United States Minor Outlying Islands", "um"),
    URUGUAY("Uruguay", "uy"),
    UZBEKISTAN("Uzbekistan", "uz"),
    VANUATU("Vanuatu", "vu"),
    VENEZUELA("Venezuela", "ve"),
    VIET_NAM("Viet Nam", "vn"),
    VIRGIN_ISLANDS_BRITISH("Virgin Islands, British", "vg"),
    VIRGIN_ISLANDS_U_S("Virgin Islands, U.S.", "vi"),
    WALLIS_AND_FUTUNA("Wallis and Futuna", "wf"),
    WESTERN_SAHARA("Western Sahara", "eh"),
    YEMEN("Yemen", "ye"),
    ZAMBIA("Zambia", "zm"),
    ZIMBABWE("Zimbabwe", "zw");

    private static final Random PRNG = new Random();
    private final String name;
    @Getter
    private final String code;

    public static CountryEnum findByCode(String code) {
        return Arrays.stream(CountryEnum.values())
                .filter(countryEnum -> code.equals(countryEnum.code))
                .findAny()
                .orElseThrow();
    }

    public static CountryEnum getRandom() {
        CountryEnum[] countryEnums = CountryEnum.values();
        return countryEnums[PRNG.nextInt(countryEnums.length)];
    }

    @Override
    public String toString() {
        return name;
    }
}
