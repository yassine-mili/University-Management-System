using System;

namespace BillingService.Services
{
    public class SoapFaultException : Exception
    {
        public SoapFaultException(string message) : base(message) { }
    }
}
