import React from 'react';
import './StatsView.css';

function StatsView({ stats, loading }) {
  if (loading) {
    return <div className="loading">⏳ Loading statistics...</div>;
  }

  if (!stats) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">📊</div>
        <p>No statistics data</p>
        <small>Select a student and click the Statistics tab</small>
      </div>
    );
  }

  const passRate = stats.total_grades > 0 
    ? ((stats.passed_grades / stats.total_grades) * 100).toFixed(1)
    : 0;

  return (
    <div className="stats-container">
      <h2>Grade Statistics</h2>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">📈</div>
          <div className="stat-content">
            <div className="stat-label">Total Grades</div>
            <div className="stat-value">{stats.total_grades || 0}</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">✅</div>
          <div className="stat-content">
            <div className="stat-label">Passed</div>
            <div className="stat-value pass">{stats.passed_grades || 0}</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">❌</div>
          <div className="stat-content">
            <div className="stat-label">Failed</div>
            <div className="stat-value fail">{stats.failed_grades || 0}</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📊</div>
          <div className="stat-content">
            <div className="stat-label">Pass Rate</div>
            <div className="stat-value">{passRate}%</div>
          </div>
        </div>
      </div>

      <div className="stats-details">
        <div className="detail-section">
          <h3>Grade Distribution</h3>
          <div className="distribution-grid">
            <div className="distribution-item">
              <span className="dist-label">Highest Grade:</span>
              <span className="dist-value">{stats.highest_grade?.toFixed(2) || 'N/A'}</span>
            </div>
            <div className="distribution-item">
              <span className="dist-label">Lowest Grade:</span>
              <span className="dist-value">{stats.lowest_grade?.toFixed(2) || 'N/A'}</span>
            </div>
            <div className="distribution-item">
              <span className="dist-label">Average Grade:</span>
              <span className="dist-value">{stats.average_grade?.toFixed(2) || 'N/A'}</span>
            </div>
            <div className="distribution-item">
              <span className="dist-label">Median Grade:</span>
              <span className="dist-value">{stats.median_grade?.toFixed(2) || 'N/A'}</span>
            </div>
          </div>
        </div>

        {stats.grade_distribution && (
          <div className="detail-section">
            <h3>Grade Ranges</h3>
            <div className="grade-ranges">
              {Object.entries(stats.grade_distribution).map(([range, count]) => (
                <div key={range} className="grade-range-item">
                  <div className="range-label">{range}</div>
                  <div className="range-bar">
                    <div 
                      className="range-fill"
                      style={{
                        width: `${(count / (stats.total_grades || 1)) * 100}%`
                      }}
                    ></div>
                  </div>
                  <div className="range-count">{count}</div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default StatsView;
