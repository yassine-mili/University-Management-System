import requests

ENDPOINT = "http://localhost:5000/InvoiceService.svc"

soap_envelope = """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <CreateInvoice xmlns="http://tempuri.org/">
        <invoiceNumber>TND-SOAP-001</invoiceNumber>
        <amount>2000.0</amount>
        <currency>TND</currency>
        <universityId>TUNSOAP001</universityId>
        <firstName>Hichem</firstName>
        <lastName>Student</lastName>
      </CreateInvoice>
  </soap:Body>
</soap:Envelope>
"""

headers = {
  'Content-Type': 'text/xml; charset=utf-8',
  'SOAPAction': 'http://tempuri.org/IInvoiceSoapService/CreateInvoice'
}

resp = requests.post(ENDPOINT, data=soap_envelope, headers=headers, timeout=15)
print('Status:', resp.status_code)
print(resp.text[:1000])
