import React from 'react';
import './TranscriptView.css';

function TranscriptView({ transcript, loading }) {
  if (loading) {
    return <div className="loading">⏳ Loading transcript...</div>;
  }

  if (!transcript) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">📄</div>
        <p>No transcript data</p>
        <small>Select a student and click the Transcript tab</small>
      </div>
    );
  }

  return (
    <div className="transcript-container">
      <div className="transcript-header">
        <h2>Academic Transcript</h2>
        <div className="transcript-meta">
          <div className="meta-item">
            <span className="meta-label">Student ID:</span>
            <span className="meta-value">{transcript.student_id}</span>
          </div>
          <div className="meta-item">
            <span className="meta-label">Generated:</span>
            <span className="meta-value">{new Date(transcript.generated_at).toLocaleDateString()}</span>
          </div>
        </div>
      </div>

      <div className="transcript-content">
        <div className="gpa-section">
          <div className="gpa-card">
            <div className="gpa-label">Current GPA</div>
            <div className="gpa-value">{transcript.gpa.toFixed(2)}</div>
            <div className="gpa-scale">out of 4.0</div>
          </div>
          <div className="status-card">
            <div className="status-label">Academic Status</div>
            <div className={`status-value ${transcript.academic_status.toLowerCase()}`}>
              {transcript.academic_status}
            </div>
          </div>
        </div>

        {transcript.courses && transcript.courses.length > 0 && (
          <div className="courses-section">
            <h3>Courses Completed</h3>
            <div className="courses-grid">
              {transcript.courses.map((course, idx) => (
                <div key={idx} className="course-card">
                  <div className="course-header">
                    <div className="course-code">{course.course_id}</div>
                    <div className={`course-grade ${course.grade >= 10 ? 'pass' : 'fail'}`}>
                      {course.grade}
                    </div>
                  </div>
                  <div className="course-info">
                    <span className="course-credits">Credits: {course.weight || 'N/A'}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {transcript.summary && (
          <div className="summary-section">
            <h3>Summary</h3>
            <div className="summary-grid">
              <div className="summary-item">
                <span className="summary-label">Total Courses:</span>
                <span className="summary-value">{transcript.summary.total_courses || 0}</span>
              </div>
              <div className="summary-item">
                <span className="summary-label">Passed:</span>
                <span className="summary-value pass">{transcript.summary.passed_courses || 0}</span>
              </div>
              <div className="summary-item">
                <span className="summary-label">Failed:</span>
                <span className="summary-value fail">{transcript.summary.failed_courses || 0}</span>
              </div>
              <div className="summary-item">
                <span className="summary-label">Avg Grade:</span>
                <span className="summary-value">{transcript.summary.average_grade?.toFixed(2) || 'N/A'}</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default TranscriptView;
