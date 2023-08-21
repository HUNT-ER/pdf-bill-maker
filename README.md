# PDF Bill Maker
This API allows you to automatically create and sign an invoice and vehicle acceptance certificate for trucking companies.
The output is a link to download a pdf of the document on google drive. Two variants of document preparation are supported - with and without signature.

## Set up 
- [*clone*](https://github.com/HUNT-ER/pdf-bill-maker.git) the project
- add Images of signatures (1 per each page, total 2) in classpath, fonts for document (bold and regular), google drive secret credentials. I recommended to use service account 
- change [application.properties](src/main/resources/application.properties.origin) file based on your database configurations and change path to signatures, fonts and google credentials.
- run the project using [BillMakerApplication.java](src/main/java/com/boldyrev/pdfbillcreator/BillMakerApplication.java) 

# API Reference 


## Get all created Bills

**GET** `/bills `
  returns list of bills


## Create new bill by details

**POST** `/bills/new`
  returns new bill

```agsl
  request body:
  {
      "number": 1,
      "date": "YYYY-mm-dd",
      "recipient_cred": "",
      "bank_cred": "",
      "carrier": "",
      "customer": "",
      "customer_cred": "",
      "route": "",
      "cost": 80000,
      "signatory": "",
      "signed": true
  }
```

# Entity diagram
![bills](https://github.com/HUNT-ER/pdf-bill-maker/assets/38404914/823fbc63-960c-46d2-9e21-643c7c0be23b)

# What I learned
- Learned Docker, Docker compose, Google Drive API, IText7 library for PDF
- Improved skills in testing and Spring Boot
