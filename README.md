# PDF Bill Maker
This API allows you to automatically create and sign an invoice and vehicle acceptance certificate for trucking companies.
The output is a link to download a pdf of the document on google drive. Two variants of document preparation are supported - with and without signature.

## Set up 
- [*clone*](https://github.com/HUNT-ER/pdf-bill-maker.git) the project
- add Images of signatures (1 per each page, total 2) in classpath, fonts for document (bold and regular), google drive secret credentials. I recommended to use service account 
- change [application.properties](src/main/resources/application.properties.origin) file based on your database configurations and change path to signs, fonts and google credentials.
- run the project using [BillMakerApplication.java](src/main/java/com/boldyrev/pdfbillcreator/BillMakerApplication.java) 
