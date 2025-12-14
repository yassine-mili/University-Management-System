import React from 'react';
import './GradesList.css';

function GradesList({ grades, loading, onDelete }) {
  if (loading) {
    return <div className="loading">⏳ Loading grades...</div>;
  }

  if (!grades || grades.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">📋</div>
        <p>No grades found</p>
        <small>Enter a Student ID and click "Load Grades" to get started</small>
      </div>
    );
  }

  return (
    <div className="grades-list-container">
      <h2>Student Grades ({grades.length})</h2>
      <div className="grades-table-wrapper">
        <table className="grades-table">
          <thead>
            <tr>
              <th>Course ID</th>
              <th>Grade</th>
              <th>Weight</th>
              <th>Exam Date</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {grades.map((grade) => (
              <tr key={grade.id} className={`grade-row ${grade.grade >= 10 ? 'pass' : 'fail'}`}>
                <td className="course-id">{grade.course_id}</td>
                <td className="grade-value">
                  <span className={`grade-badge ${grade.grade >= 10 ? 'pass' : 'fail'}`}>
                    {grade.grade}
                  </span>
                </td>
                <td>{grade.weight}</td>
                <td>{new Date(grade.exam_date).toLocaleDateString()}</td>
                <td>
                  <span className={`status-badge ${grade.grade >= 10 ? 'pass' : 'fail'}`}>
                    {grade.grade >= 10 ? '✓ Pass' : '✗ Fail'}
                  </span>
                </td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => onDelete(grade.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default GradesList;
