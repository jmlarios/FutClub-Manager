import api from './api';

const playerService = {
  getAllPlayers: async () => {
    const response = await api.get('/players');
    return response.data;
  },

  getPlayerById: async (id) => {
    const response = await api.get(`/players/${id}`);
    return response.data;
  },

  getPlayersByTeam: async (teamId) => {
    const response = await api.get(`/teams/${teamId}/players`);
    return response.data;
  },

  createPlayer: async (playerData) => {
    const response = await api.post('/players', playerData);
    return response.data;
  },

  updatePlayer: async (id, playerData) => {
    const response = await api.put(`/players/${id}`, playerData);
    return response.data;
  },

  deletePlayer: async (id) => {
    const response = await api.delete(`/players/${id}`);
    return response.data;
  },

  getPlayerStats: async (id) => {
    const response = await api.get(`/players/${id}/stats`);
    return response.data;
  },
};

export default playerService;