import React, { useState } from 'react';
import axios from 'axios';
import './GradeForm.css';

function GradeForm({ studentId, onGradeAdded }) {
  const [formData, setFormData] = useState({
    student_id: studentId || '',
    course_id: '',
    grade: '',
    weight: '1',
    exam_date: new Date().toISOString().split('T')[0],
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const API_BASE = 'http://localhost:8084';

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');

    if (!formData.student_id || !formData.course_id || !formData.grade) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    try {
      await axios.post(`${API_BASE}/grades/`, {
        student_id: formData.student_id,
        course_id: formData.course_id,
        grade: parseFloat(formData.grade),
        weight: parseFloat(formData.weight),
        exam_date: formData.exam_date,
      });
      setMessage('✅ Grade added successfully!');
      setFormData({
        student_id: studentId || '',
        course_id: '',
        grade: '',
        weight: '1',
        exam_date: new Date().toISOString().split('T')[0],
      });
      onGradeAdded();
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setError(err.response?.data?.detail || 'Failed to add grade');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grade-form-container">
      <h2>Add New Grade</h2>
      
      {error && <div className="form-error">{error}</div>}
      {message && <div className="form-success">{message}</div>}

      <form onSubmit={handleSubmit} className="grade-form">
        <div className="form-group">
          <label htmlFor="student_id">Student ID *</label>
          <input
            type="text"
            id="student_id"
            name="student_id"
            value={formData.student_id}
            onChange={handleChange}
            placeholder="e.g., STU001"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="course_id">Course ID *</label>
          <input
            type="text"
            id="course_id"
            name="course_id"
            value={formData.course_id}
            onChange={handleChange}
            placeholder="e.g., MATH101"
            required
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="grade">Grade (0-20) *</label>
            <input
              type="number"
              id="grade"
              name="grade"
              value={formData.grade}
              onChange={handleChange}
              min="0"
              max="20"
              step="0.5"
              placeholder="15.5"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="weight">Weight</label>
            <input
              type="number"
              id="weight"
              name="weight"
              value={formData.weight}
              onChange={handleChange}
              min="0.1"
              step="0.1"
              placeholder="1"
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="exam_date">Exam Date</label>
          <input
            type="date"
            id="exam_date"
            name="exam_date"
            value={formData.exam_date}
            onChange={handleChange}
          />
        </div>

        <button 
          type="submit" 
          className="btn btn-submit"
          disabled={loading}
        >
          {loading ? 'Adding...' : 'Add Grade'}
        </button>
      </form>
    </div>
  );
}

export default GradeForm;
