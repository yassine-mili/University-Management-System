using System.Runtime.Serialization;

namespace Universite.BillingService.DTOs;

[DataContract]
public class FeeStructureDTO
{
    [DataMember]
    public int Id { get; set; }

    [DataMember]
    public string FeeType { get; set; } = string.Empty;

    [DataMember]
    public string FeeName { get; set; } = string.Empty;

    [DataMember]
    public decimal Amount { get; set; }

    [DataMember]
    public string Semester { get; set; } = string.Empty;

    [DataMember]
    public string AcademicYear { get; set; } = string.Empty;

    [DataMember]
    public bool IsActive { get; set; }
}
