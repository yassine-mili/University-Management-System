using System;
using System.Runtime.Serialization;

namespace BillingService.Models
{
    [DataContract(IsReference = true)]
    public class Invoice
    {
        [DataMember]
        public int Id { get; set; }

        [DataMember]
        public string InvoiceNumber { get; set; } = null!;

        [DataMember]
        public decimal Amount { get; set; }

        [DataMember]
        public string? Currency { get; set; }

        [DataMember]
        public DateTime CreatedAt { get; set; }

        [DataMember]
        public bool IsPaid { get; set; }

        [DataMember]
        public int StudentId { get; set; }

        [DataMember]
        public Student? Student { get; set; }
    }
}
