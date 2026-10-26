package com.example.core.utils

data class Wilaya(
  val code: String,
  val nameFr: String,
  val nameAr: String,
  val communes: List<String> = emptyList()
)

object AlgerianData {

  val LEGAL_FORMS = listOf(
    "SARL" to "Société à Responsabilité Limitée",
    "EURL" to "Entreprise Unipersonnelle à Responsabilité Limitée",
    "SPA" to "Société par Actions",
    "SNC" to "Société en Nom Collectif",
    "SCS" to "Société en Commandite Simple",
    "Auto-entrepreneur" to "Statut Auto-entrepreneur (Décret 23-197)",
    "Personne physique" to "Commerçant Individuel / Artisan"
  )

  val ALGERIAN_BANKS = listOf(
    "BNA" to "Banque Nationale d'Algérie",
    "BEA" to "Banque Extérieure d'Algérie",
    "CPA" to "Crédit Populaire d'Algérie",
    "BDL" to "Banque de Développement Local",
    "BADR" to "Banque de l'Agriculture et du Développement Rural",
    "CNEP" to "Caisse Nationale d'Épargne et de Prévoyance",
    "Algérie Poste (CCP)" to "Algérie Poste - Compte Courant Postal",
    "Al Baraka" to "Banque Al Baraka d'Algérie",
    "Société Générale" to "Société Générale Algérie",
    "BNP Paribas" to "BNP Paribas El Djazaïr",
    "AGB" to "Gulf Bank Algérie (AGB)",
    "Natixis" to "Natixis Algérie",
    "Al Salam" to "Al Salam Bank Algeria",
    "Fransabank" to "Fransabank El Djazaïr",
    "Housing Bank" to "Housing Bank for Trade and Finance Algérie",
    "Arab Bank" to "Arab Bank PLC Algeria",
    "Trust Bank" to "Trust Bank Algeria"
  )

  val CATEGORIES = listOf(
    "Fournitures & Consommables",
    "Matériaux de construction & BTP",
    "Outillage & Quincaillerie",
    "Équipements & Machines",
    "Informatique, Télécom & Réseaux",
    "Électricité & Électronique",
    "Prestations de services & Conseil",
    "Transport, Logistique & Fret",
    "Pièces de rechange industrielles",
    "Sécurité & Équipements EPI",
    "Mobilier & Aménagement de bureau"
  )

  val WILAYAS: List<Wilaya> = listOf(
    Wilaya("01", "Adrar", "أدرار", listOf("Adrar", "Tamest", "Charouine", "Reggane", "Aoulef")),
    Wilaya("02", "Chlef", "الشلف", listOf("Chlef", "Ténès", "El Karimia", "Boukadir", "Oued Fodda")),
    Wilaya("03", "Laghouat", "الأغواط", listOf("Laghouat", "Aflou", "Hassi R'Mel", "Ksar El Hirane")),
    Wilaya("04", "Oum El Bouaghi", "أم البواقي", listOf("Oum El Bouaghi", "Aïn Beïda", "Aïn M'lila")),
    Wilaya("05", "Batna", "باتنة", listOf("Batna", "Barika", "Aïn Touta", "Arris", "Merouana")),
    Wilaya("06", "Béjaïa", "بجاية", listOf("Béjaïa", "Akbou", "Amizour", "Seddouk", "El Kseur")),
    Wilaya("07", "Biskra", "بسكرة", listOf("Biskra", "Tolga", "Sidi Okba", "Ouled Djellal")),
    Wilaya("08", "Béchar", "بشار", listOf("Béchar", "Abadla", "Béni Ounif", "Kenadsa")),
    Wilaya("09", "Blida", "البليدة", listOf("Blida", "Boufarik", "Ouled Yaïch", "El Affroun", "Larbaâ")),
    Wilaya("10", "Bouira", "البويرة", listOf("Bouira", "Lakhdaria", "Sour El Ghozlane", "Aïn Bessem")),
    Wilaya("11", "Tamanrasset", "تمنراست", listOf("Tamanrasset", "Abalessa", "Idlès", "Tazrouk")),
    Wilaya("12", "Tébessa", "تبسة", listOf("Tébessa", "Chréa", "Bir El Ater", "Ouenza")),
    Wilaya("13", "Tlemcen", "تلمسان", listOf("Tlemcen", "Mansourah", "Chetouane", "Maghnia", "Remchi", "Ghazaouet")),
    Wilaya("14", "Tiaret", "تيارت", listOf("Tiaret", "Sougueur", "Frenda", "Ksar Chellala")),
    Wilaya("15", "Tizi Ouzou", "تيزي وزو", listOf("Tizi Ouzou", "Azazga", "Draâ Ben Khedda", "Boghni", "Larbaâ Nath Irathen")),
    Wilaya("16", "Alger", "الجزائر", listOf("Alger Centre", "Sidi M'Hamed", "Bab El Oued", "Hydra", "Bir Mourad Raïs", "Kouba", "Hussein Dey", "Dar El Beïda", "Bab Ezzouar", "Chéraga", "Zéralda", "Rouïba")),
    Wilaya("17", "Djelfa", "الجلفة", listOf("Djelfa", "Aïn Oussera", "Messaâd", "Hassi Bahbah")),
    Wilaya("18", "Jijel", "جيجل", listOf("Jijel", "Taher", "El Milia", "El Ancer")),
    Wilaya("19", "Sétif", "سطيف", listOf("Sétif", "El Eulma", "Aïn Oulmene", "Aïn Arnat", "Bougaa")),
    Wilaya("20", "Saïda", "سعيدة", listOf("Saïda", "Youb", "Aïn El Hadjar")),
    Wilaya("21", "Skikda", "سكيكدة", listOf("Skikda", "El Harrouch", "Collo", "Azzaba", "Tamalous")),
    Wilaya("22", "Sidi Bel Abbès", "سيدي بلعباس", listOf("Sidi Bel Abbès", "Telagh", "Ben Badis", "Sfier")),
    Wilaya("23", "Annaba", "عنابة", listOf("Annaba", "El Bouni", "El Hadjar", "Berrahal", "Seraïdi")),
    Wilaya("24", "Guelma", "قالمة", listOf("Guelma", "Oued Zenati", "Bouchegouf", "Héliopolis")),
    Wilaya("25", "Constantine", "قسنطينة", listOf("Constantine", "El Khroub", "Hamma Bouziane", "Didouche Mourad", "Zighoud Youcef", "Ali Mendjeli")),
    Wilaya("26", "Médéa", "المدية", listOf("Médéa", "Berrouaghia", "Ksar El Boukhari", "Beni Slimane")),
    Wilaya("27", "Mostaganem", "مستغانم", listOf("Mostaganem", "Aïn Tedeles", "Sidi Ali", "Hassi Mameche")),
    Wilaya("28", "M'Sila", "المسيلة", listOf("M'Sila", "Bou Saâda", "Sidi Aïssa", "Magra")),
    Wilaya("29", "Mascara", "معسكر", listOf("Mascara", "Sig", "Tighennif", "Mohammadia")),
    Wilaya("30", "Ouargla", "ورقلة", listOf("Ouargla", "Hassi Messaoud", "Rouissat", "N'Goussa")),
    Wilaya("31", "Oran", "وهران", listOf("Oran", "Bir El Djir", "Es Senia", "Arzew", "Aïn El Turk", "Bethioua")),
    Wilaya("32", "El Bayadh", "البيض", listOf("El Bayadh", "Rogassa", "Brezina")),
    Wilaya("33", "Illizi", "إليزي", listOf("Illizi", "In Aménas", "Debdeb")),
    Wilaya("34", "Bordj Bou Arréridj", "برج بوعريريج", listOf("Bordj Bou Arréridj", "Ras El Oued", "Mansoura")),
    Wilaya("35", "Boumerdès", "بومرداس", listOf("Boumerdès", "Khemis El Khechna", "Boudouaou", "Dellys", "Thénia")),
    Wilaya("36", "El Tarf", "الطارف", listOf("El Tarf", "El Kala", "Ben M'Hidi", "Besbes")),
    Wilaya("37", "Tindouf", "تندوف", listOf("Tindouf", "Oum El Assel")),
    Wilaya("38", "Tissemsilt", "تيسمسيلت", listOf("Tissemsilt", "Théniet El Had", "Lardjem")),
    Wilaya("39", "El Oued", "الوادي", listOf("El Oued", "Robbah", "Guemar", "Debila")),
    Wilaya("40", "Khenchela", "خنشلة", listOf("Khenchela", "Chechar", "Kaïs", "Bouhmama")),
    Wilaya("41", "Souk Ahras", "سوق أهراس", listOf("Souk Ahras", "Sedrata", "M'Daourouch", "Taoura")),
    Wilaya("42", "Tipaza", "تيبازة", listOf("Tipaza", "Cherchell", "Kolea", "Hadout", "Bou Ismaïl")),
    Wilaya("43", "Mila", "ميلة", listOf("Mila", "Chelghoum Laïd", "Tadjenanet", "Grarem Gouga")),
    Wilaya("44", "Aïn Defla", "عين الدفلى", listOf("Aïn Defla", "Khemis Miliana", "Miliana", "El Attaf")),
    Wilaya("45", "Naâma", "النعامة", listOf("Naâma", "Mécheria", "Aïn Sefra")),
    Wilaya("46", "Aïn Témouchent", "عين تموشنت", listOf("Aïn Témouchent", "Beni Saf", "Hammam Bou Hadjar")),
    Wilaya("47", "Ghardaïa", "غرداية", listOf("Ghardaïa", "El Guerara", "Metlili", "Bounoura", "Dhayet Ben Dhahab")),
    Wilaya("48", "Relizane", "غليزان", listOf("Relizane", "Oued Rhiou", "Mazouna", "Zemmora")),
    Wilaya("49", "Timimoun", "تيميمون", listOf("Timimoun", "Aougrout", "Tinerkouk")),
    Wilaya("50", "Bordj Badji Mokhtar", "برج باجي مختار", listOf("Bordj Badji Mokhtar", "Timiaouine")),
    Wilaya("51", "Ouled Djellal", "أولاد جلال", listOf("Ouled Djellal", "Sidi Khaled")),
    Wilaya("52", "Béni Abbès", "بني عباس", listOf("Béni Abbès", "Kerzaz", "Tabelbala")),
    Wilaya("53", "In Salah", "عين صالح", listOf("In Salah", "In Ghar", "Foggaret Ezzaouia")),
    Wilaya("54", "In Guezzam", "عين قزام", listOf("In Guezzam", "Tin Zaouatine")),
    Wilaya("55", "Touggourt", "تقرت", listOf("Touggourt", "Temacine", "Megarine")),
    Wilaya("56", "Djanet", "جانت", listOf("Djanet", "Bordj El Haouas")),
    Wilaya("57", "El M'Ghair", "المغير", listOf("El M'Ghair", "Djamaa")),
    Wilaya("58", "El Meniaa", "المنيعة", listOf("El Meniaa", "Hassi Gara"))
  )
}
