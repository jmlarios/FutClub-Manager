import api from './api';

const matchService = {
  getAllMatches: async () => {
    const response = await api.get('/matches');
    return response.data;
  },

  getMatchById: async (id) => {
    const response = await api.get(`/matches/${id}`);
    return response.data;
  },

  getMatchesByTeam: async (teamId) => {
    const response = await api.get(`/teams/${teamId}/matches`);
    return response.data;
  },

  createMatch: async (matchData) => {
    const response = await api.post('/matches', matchData);
    return response.data;
  },

  updateMatch: async (id, matchData) => {
    const response = await api.put(`/matches/${id}`, matchData);
    return response.data;
  },

  deleteMatch: async (id) => {
    const response = await api.delete(`/matches/${id}`);
    return response.data;
  },

  updateMatchScore: async (id, scoreData) => {
    const response = await api.patch(`/matches/${id}/score`, scoreData);
    return response.data;
  },
};

export default matchService;