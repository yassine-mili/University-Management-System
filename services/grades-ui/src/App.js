import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import GradeForm from './components/GradeForm';
import GradesList from './components/GradesList';
import TranscriptView from './components/TranscriptView';
import StatsView from './components/StatsView';

function App() {
  const [grades, setGrades] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('grades');
  const [studentId, setStudentId] = useState('');
  const [transcript, setTranscript] = useState(null);
  const [stats, setStats] = useState(null);

  const API_BASE = 'http://localhost:8084';

  const fetchGrades = async (id) => {
    if (!id) {
      setError('Please enter a Student ID');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(`${API_BASE}/grades/student/${id}`);
      setGrades(response.data);
    } catch (err) {
      setError(err.response?.data?.detail || 'Failed to fetch grades');
      setGrades([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchTranscript = async (id) => {
    if (!id) {
      setError('Please enter a Student ID');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(`${API_BASE}/grades/student/${id}/transcript`);
      setTranscript(response.data);
    } catch (err) {
      setError(err.response?.data?.detail || 'Failed to fetch transcript');
      setTranscript(null);
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async (id) => {
    if (!id) {
      setError('Please enter a Student ID');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(`${API_BASE}/grades/student/${id}/statistics`);
      setStats(response.data);
    } catch (err) {
      setError(err.response?.data?.detail || 'Failed to fetch statistics');
      setStats(null);
    } finally {
      setLoading(false);
    }
  };

  const handleGradeAdded = () => {
    if (studentId) {
      fetchGrades(studentId);
    }
  };

  const handleDeleteGrade = async (gradeId) => {
    if (!window.confirm('Are you sure you want to delete this grade?')) return;
    
    setLoading(true);
    setError(null);
    try {
      await axios.delete(`${API_BASE}/grades/${gradeId}`);
      if (studentId) {
        fetchGrades(studentId);
      }
    } catch (err) {
      setError(err.response?.data?.detail || 'Failed to delete grade');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>📊 Grades Service Testing</h1>
        <p>Simple UI for managing and testing grades</p>
      </header>

      <div className="container">
        <div className="student-input-section">
          <input
            type="text"
            placeholder="Enter Student ID"
            value={studentId}
            onChange={(e) => setStudentId(e.target.value)}
            className="student-input"
          />
          <button 
            onClick={() => fetchGrades(studentId)}
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'Loading...' : 'Load Grades'}
          </button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="tabs">
          <button 
            className={`tab ${activeTab === 'grades' ? 'active' : ''}`}
            onClick={() => setActiveTab('grades')}
          >
            Grades
          </button>
          <button 
            className={`tab ${activeTab === 'add' ? 'active' : ''}`}
            onClick={() => setActiveTab('add')}
          >
            Add Grade
          </button>
          <button 
            className={`tab ${activeTab === 'transcript' ? 'active' : ''}`}
            onClick={() => {
              setActiveTab('transcript');
              fetchTranscript(studentId);
            }}
          >
            Transcript
          </button>
          <button 
            className={`tab ${activeTab === 'stats' ? 'active' : ''}`}
            onClick={() => {
              setActiveTab('stats');
              fetchStats(studentId);
            }}
          >
            Statistics
          </button>
        </div>

        <div className="content">
          {activeTab === 'grades' && (
            <GradesList 
              grades={grades} 
              loading={loading}
              onDelete={handleDeleteGrade}
            />
          )}
          {activeTab === 'add' && (
            <GradeForm 
              studentId={studentId}
              onGradeAdded={handleGradeAdded}
            />
          )}
          {activeTab === 'transcript' && (
            <TranscriptView 
              transcript={transcript}
              loading={loading}
            />
          )}
          {activeTab === 'stats' && (
            <StatsView 
              stats={stats}
              loading={loading}
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
