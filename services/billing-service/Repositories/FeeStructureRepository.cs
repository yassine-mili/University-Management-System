using Microsoft.EntityFrameworkCore;
using Universite.BillingService.Models;

namespace Universite.BillingService.Repositories;

public class FeeStructureRepository : IFeeStructureRepository
{
    private readonly BillingDbContext _context;

    public FeeStructureRepository(BillingDbContext context)
    {
        _context = context;
    }

    public async Task<FeeStructure?> GetByIdAsync(int id)
    {
        return await _context.FeeStructures.FindAsync(id);
    }

    public async Task<List<FeeStructure>> GetAllAsync()
    {
        return await _context.FeeStructures
            .OrderBy(f => f.FeeType)
            .ToListAsync();
    }

    public async Task<List<FeeStructure>> GetBySemesterAndYearAsync(string? semester, string? academicYear)
    {
        var query = _context.FeeStructures.AsQueryable();

        if (!string.IsNullOrEmpty(semester))
        {
            query = query.Where(f => f.Semester == semester);
        }

        if (!string.IsNullOrEmpty(academicYear))
        {
            query = query.Where(f => f.AcademicYear == academicYear);
        }

        return await query.OrderBy(f => f.FeeType).ToListAsync();
    }

    public async Task<List<FeeStructure>> GetActiveFeesAsync()
    {
        return await _context.FeeStructures
            .Where(f => f.IsActive)
            .OrderBy(f => f.FeeType)
            .ToListAsync();
    }
}
