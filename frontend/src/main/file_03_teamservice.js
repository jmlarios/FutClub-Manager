import api from './api';

const teamService = {
  getAllTeams: async () => {
    const response = await api.get('/teams');
    return response.data;
  },

  getTeamById: async (id) => {
    const response = await api.get(`/teams/${id}`);
    return response.data;
  },

  getUserTeams: async () => {
    const response = await api.get('/teams/user');
    return response.data;
  },

  createTeam: async (teamData) => {
    const response = await api.post('/teams', teamData);
    return response.data;
  },

  updateTeam: async (id, teamData) => {
    const response = await api.put(`/teams/${id}`, teamData);
    return response.data;
  },

  deleteTeam: async (id) => {
    const response = await api.delete(`/teams/${id}`);
    return response.data;
  },

  getTeamStats: async (id) => {
    const response = await api.get(`/teams/${id}/stats`);
    return response.data;
  },
};

export default teamService;