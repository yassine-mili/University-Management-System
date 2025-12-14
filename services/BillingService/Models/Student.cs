using System.Collections.Generic;
using System.Runtime.Serialization;

namespace BillingService.Models
{
    [DataContract(IsReference = true)]
    public class Student
    {
        [DataMember]
        public int Id { get; set; }

        [DataMember]
        public int UniversityId { get; set; }

        [DataMember]
        public string FirstName { get; set; } = null!;

        [DataMember]
        public string LastName { get; set; } = null!;

        [DataMember]
        public List<Invoice>? Invoices { get; set; }
    }
}
