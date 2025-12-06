namespace BillingService.Models;

public class Student
{
    public int Id { get; set; }
    public string UniversityId { get; set; } = string.Empty;
    public string FirstName { get; set; } = string.Empty;
    public string LastName { get; set; } = string.Empty;
}
