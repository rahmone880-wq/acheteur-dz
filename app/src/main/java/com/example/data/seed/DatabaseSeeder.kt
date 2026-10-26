package com.example.data.seed

import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SettingsEntity
import com.example.data.local.entities.SupplierBankAccountEntity
import com.example.data.local.entities.SupplierDocumentEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInteractionEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import com.example.data.repository.ProcurementRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.ProjectRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.SupplierRepository
import kotlinx.coroutines.flow.first

object DatabaseSeeder {

  suspend fun seedIfEmpty(
    supplierRepository: SupplierRepository,
    projectRepository: ProjectRepository,
    productRepository: ProductRepository,
    procurementRepository: ProcurementRepository,
    settingsRepository: SettingsRepository
  ) {
    val existing = supplierRepository.getAllSuppliersIncludingArchived().first()
    if (existing.isNotEmpty()) return

    // Ensure default settings
    settingsRepository.updateSettings(SettingsEntity())

    // 1. Fournisseurs Algériens
    val sup1 = SupplierEntity(
      id = "sup-alg-001",
      raisonSociale = "SARL Algerian Industrial Supply",
      formeJuridique = "SARL",
      activite = "Fourniture industrielle, quincaillerie lourde et outillage professionnel",
      adresse = "Zone Industrielle Oued Smar, Lot N° 14",
      wilayaCode = "16",
      wilayaName = "Alger",
      commune = "Oued Smar",
      phone = "023924510",
      email = "contact@alg-industrial.dz",
      contactPerson = "Karim Mansouri (Directeur Commercial)",
      rcNumber = "16/00-0987654B19",
      rcDate = "15/03/2019",
      nif = "001916000987654",
      nis = "001916000987654",
      articleImposition = "16024892150",
      status = "AGREE",
      qualityScore = 4.8,
      priceScore = 4.2,
      deliveryScore = 4.6,
      afterSalesScore = 4.5,
      reliabilityScore = 4.9,
      overallRating = 4.6,
      paymentTermsDays = 30,
      leadTimeDays = 5,
      deliveryZones = "Alger, Blida, Boumerdès, Tipaza, Oran, Constantine",
      categoriesTags = "Outillage & Quincaillerie, Équipements & Machines"
    )
    supplierRepository.saveSupplier(sup1)

    supplierRepository.addBankAccount(
      SupplierBankAccountEntity(
        id = "bank-001",
        supplierId = sup1.id,
        bankName = "BNA",
        agency = "Agence Chéraga 612",
        rib = "00100612012345678912",
        holderName = "SARL ALGERIAN INDUSTRIAL SUPPLY",
        isPrimary = true
      )
    )

    supplierRepository.addDocument(
      SupplierDocumentEntity(
        id = "doc-001",
        supplierId = sup1.id,
        docType = "RC",
        title = "Extrait du Registre de Commerce (CNRC)",
        referenceNumber = "CNRC-16-0987654",
        issueDate = "15/01/2026",
        expiryDate = "15/01/2027"
      )
    )

    val sup2 = SupplierEntity(
      id = "sup-oran-002",
      raisonSociale = "EURL BTP Méditerranée",
      formeJuridique = "EURL",
      activite = "Matériaux de construction, ferraillage, ciment et plâtrerie",
      adresse = "Zone d'Activité Bir El Djir",
      wilayaCode = "31",
      wilayaName = "Oran",
      commune = "Bir El Djir",
      phone = "041538090",
      email = "commercial@btp-med.dz",
      contactPerson = "Nabil Bouzid",
      rcNumber = "31/00-1122334B21",
      rcDate = "10/06/2021",
      nif = "002131001122334",
      nis = "002131001122334",
      articleImposition = "31051289431",
      status = "AGREE",
      qualityScore = 4.2,
      priceScore = 4.5,
      deliveryScore = 3.8,
      afterSalesScore = 4.0,
      reliabilityScore = 4.1,
      overallRating = 4.1,
      paymentTermsDays = 45,
      leadTimeDays = 3,
      deliveryZones = "Oran, Mostaganem, Sidi Bel Abbès, Tlemcen",
      categoriesTags = "Matériaux de construction & BTP"
    )
    supplierRepository.saveSupplier(sup2)

    supplierRepository.addBankAccount(
      SupplierBankAccountEntity(
        id = "bank-002",
        supplierId = sup2.id,
        bankName = "BEA",
        agency = "Agence Oran Port 014",
        rib = "00200014098765432198",
        holderName = "EURL BTP MEDITERRANEE",
        isPrimary = true
      )
    )

    val sup3 = SupplierEntity(
      id = "sup-stf-004",
      raisonSociale = "Amine Tech Solutions",
      formeJuridique = "Auto-entrepreneur",
      activite = "Maintenance informatique, installation caméras et contrôle d'accès",
      adresse = "Avenue 8 Mai 1945, Sétif",
      wilayaCode = "19",
      wilayaName = "Sétif",
      commune = "Sétif",
      phone = "0550123456",
      email = "aminetech@gmail.com",
      contactPerson = "Amine Khelifi",
      rcNumber = "NIN-2023-88741",
      carteArtisanNin = "NIN-2023-88741",
      nif = "199419010054321",
      nis = "199419010054321",
      articleImposition = "19045521098",
      status = "AGREE",
      qualityScore = 4.9,
      priceScore = 4.8,
      deliveryScore = 4.7,
      afterSalesScore = 5.0,
      reliabilityScore = 4.9,
      overallRating = 4.9,
      paymentTermsDays = 0,
      leadTimeDays = 2,
      deliveryZones = "Sétif, Bordj Bou Arréridj, Mila",
      categoriesTags = "Prestations de services & Conseil, Informatique, Télécom & Réseaux"
    )
    supplierRepository.saveSupplier(sup3)

    // 2. Chantiers / Projets Cibles (Project-directed purchasing)
    val prj1 = ProjectEntity(
      id = "prj-rouiba-01",
      code = "PRJ-2026-ALG01",
      name = "Chantier Tour Résidentielle Rouïba",
      client = "Promotion Immobilière Al Badr",
      locationWilaya = "16",
      locationCommune = "Rouïba",
      costCenter = "CC-ALG-BTP-01",
      manager = "Mourad Belkacem",
      status = "ACTIF",
      totalBudgetDzd = 25000000.0,
      committedBudgetDzd = 4850000.0,
      invoicedBudgetDzd = 3950000.0,
      paidBudgetDzd = 3100000.0
    )
    projectRepository.saveProject(prj1)

    val prj2 = ProjectEntity(
      id = "prj-oran-02",
      code = "PRJ-2026-ORN02",
      name = "Complexe Logistique Es Senia",
      client = "Maghreb Freight Logistics",
      locationWilaya = "31",
      locationCommune = "Es Senia",
      costCenter = "CC-ORN-LOG-02",
      manager = "Hichem Rahmouni",
      status = "ACTIF",
      totalBudgetDzd = 38000000.0,
      committedBudgetDzd = 2800000.0,
      invoicedBudgetDzd = 1500000.0,
      paidBudgetDzd = 1200000.0
    )
    projectRepository.saveProject(prj2)

    // 3. Articles & Produits (reliés aux fournisseurs par défaut)
    val prod1 = ProductEntity(
      id = "prod-ciment-01",
      reference = "MAT-CIM-001",
      designationFr = "Ciment Portland CEM II 42.5 (Sac 50kg)",
      designationAr = "إسمنت بورتلاند 42.5 كيس 50 كغ",
      category = "Matériaux de construction & BTP",
      unit = "Sac",
      tvaRate = 19.0,
      defaultSupplierId = sup2.id,
      defaultSupplierName = sup2.raisonSociale,
      lastPriceDzd = 650.0,
      minPriceDzd = 620.0,
      maxPriceDzd = 680.0,
      avgPriceDzd = 650.0
    )
    productRepository.saveProduct(prod1)

    val prod2 = ProductEntity(
      id = "prod-fer-02",
      reference = "MAT-FER-012",
      designationFr = "Rond à béton Cranté FE 500 D12 (Tonne)",
      designationAr = "حديد تسليح عالي المقاومة قطر 12 مم",
      category = "Matériaux de construction & BTP",
      unit = "Tonne",
      tvaRate = 19.0,
      defaultSupplierId = sup2.id,
      defaultSupplierName = sup2.raisonSociale,
      lastPriceDzd = 112000.0,
      minPriceDzd = 108000.0,
      maxPriceDzd = 115000.0,
      avgPriceDzd = 112000.0
    )
    productRepository.saveProduct(prod2)

    val prod3 = ProductEntity(
      id = "prod-meul-03",
      reference = "OUT-MEUL-230",
      designationFr = "Meuleuse d'angle 230mm 2400W Pro",
      designationAr = "صاروخ تقطيع احترافي 230 مم",
      category = "Outillage & Quincaillerie",
      unit = "U",
      tvaRate = 19.0,
      defaultSupplierId = sup1.id,
      defaultSupplierName = sup1.raisonSociale,
      lastPriceDzd = 28500.0,
      minPriceDzd = 26000.0,
      maxPriceDzd = 31000.0,
      avgPriceDzd = 28500.0
    )
    productRepository.saveProduct(prod3)

    val prod4 = ProductEntity(
      id = "prod-cam-04",
      reference = "SEC-CAM-004",
      designationFr = "Caméra IP Surveillance Dôme 4MP PoE Extérieure",
      designationAr = "كاميرا مراقبة خارجية 4 ميغابكسل",
      category = "Informatique, Télécom & Réseaux",
      unit = "U",
      tvaRate = 19.0,
      defaultSupplierId = sup3.id,
      defaultSupplierName = sup3.raisonSociale,
      lastPriceDzd = 14500.0,
      minPriceDzd = 13500.0,
      maxPriceDzd = 16000.0,
      avgPriceDzd = 14500.0
    )
    productRepository.saveProduct(prod4)

    // 4. Demande d'Achat (DA) liée au Projet 1
    val da1 = PurchaseRequestEntity(
      id = "da-2026-001",
      daNumber = "DA-2026-0001",
      requesterName = "Mourad Belkacem (Chef de Chantier)",
      department = "Génie Civil",
      projectId = prj1.id,
      projectName = prj1.name,
      priority = "URGENTE",
      requiredDate = "28/10/2026",
      status = "VALIDEE",
      justification = "Coulage dalle niveau R+3 prévu début de semaine prochaine.",
      totalEstimatedDzd = 2850000.0
    )
    procurementRepository.savePurchaseRequest(da1)

    // 5. Bon de Commande (BC) liant Fournisseur 2 au Projet 1 avec les Articles
    val bc1 = PurchaseOrderEntity(
      id = "bc-2026-001",
      bcNumber = "BC-2026-0001",
      purchaseRequestId = da1.id,
      daNumber = da1.daNumber,
      supplierId = sup2.id,
      supplierName = sup2.raisonSociale,
      projectId = prj1.id,
      projectName = prj1.name,
      status = "RECU",
      orderDate = "20/10/2026",
      expectedDeliveryDate = "25/10/2026",
      deliverySiteAddress = "Chantier Rouïba, Zone d'extension Badr",
      totalHtDzd = 2394958.0,
      totalTvaDzd = 455042.0,
      timbreFiscalDzd = 0.0,
      totalTtcDzd = 2850000.0,
      paymentTerms = "45 jours (Virement BEA)"
    )

    val linesBc1 = listOf(
      PurchaseOrderLineEntity(
        id = "line-bc1-01",
        purchaseOrderId = bc1.id,
        productId = prod1.id,
        productReference = prod1.reference,
        designation = prod1.designationFr,
        unit = "Sac",
        quantity = 2000.0,
        unitPriceHt = 546.22,
        tvaRate = 19.0,
        totalHt = 1092440.0,
        totalTtc = 1300000.0,
        receivedQuantity = 2000.0
      ),
      PurchaseOrderLineEntity(
        id = "line-bc1-02",
        purchaseOrderId = bc1.id,
        productId = prod2.id,
        productReference = prod2.reference,
        designation = prod2.designationFr,
        unit = "Tonne",
        quantity = 13.84,
        unitPriceHt = 94112.57,
        tvaRate = 19.0,
        totalHt = 1302518.0,
        totalTtc = 1550000.0,
        receivedQuantity = 13.84
      )
    )
    procurementRepository.savePurchaseOrder(bc1, linesBc1)

    // 6. Réception directe sur chantier (BL/BR)
    val rec1 = ReceptionEntity(
      id = "rec-2026-001",
      blNumber = "BL-BTP-9941",
      brNumber = "BR-2026-0001",
      purchaseOrderId = bc1.id,
      bcNumber = bc1.bcNumber,
      supplierId = sup2.id,
      supplierName = sup2.raisonSociale,
      destinationType = "PROJET",
      destinationId = prj1.id,
      destinationName = prj1.name,
      receptionDate = "24/10/2026",
      receivedBy = "Mourad Belkacem",
      status = "CONFORME",
      notes = "Livraison directe sur chantier Rouïba. Déchargement et contrôle qualité conformes."
    )
    procurementRepository.saveReception(rec1)

    // 7. Facture fournisseur avec rapprochement 3 voies (3-way matching)
    val inv1 = SupplierInvoiceEntity(
      id = "inv-2026-001",
      invoiceNumber = "FACT-2026-118",
      invoiceDate = "24/10/2026",
      dueDate = "08/12/2026",
      supplierId = sup2.id,
      supplierName = sup2.raisonSociale,
      projectId = prj1.id,
      projectName = prj1.name,
      purchaseOrderId = bc1.id,
      bcNumber = bc1.bcNumber,
      totalHtDzd = 2394958.0,
      totalTvaDzd = 455042.0,
      totalTtcDzd = 2850000.0,
      paidAmountDzd = 2000000.0,
      paymentStatus = "PARTIELLE",
      matchingStatus = "CONFORME"
    )
    procurementRepository.saveInvoice(inv1)
  }
}
