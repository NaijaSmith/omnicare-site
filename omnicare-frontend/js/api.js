const API_BASE_URL = 'http://localhost:8080/api/v1';

class OmnicareAPI {
    static async request(endpoint, method = 'GET', data = null) {
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };

        const config = {
            method,
            headers
        };

        if (data) {
            config.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.error || `HTTP Error ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`[API ERROR] ${method} ${endpoint}:`, error);
            throw error;
        }
    }

    static registerClient(clientData) {
        return this.request('/clients', 'POST', clientData);
    }

    static getClients() {
        return this.request('/clients', 'GET');
    }

    static updateClientStatus(id, status) {
        return this.request(`/clients/${id}/status?status=${status}`, 'PATCH');
    }

    static createTicket(ticketData) {
        return this.request('/tickets', 'POST', ticketData);
    }

    static getClientTickets(clientId) {
        return this.request(`/tickets/client/${clientId}`, 'GET');
    }

    static updateTicketStatus(ticketId, status) {
        return this.request(`/tickets/${ticketId}/status?status=${status}`, 'PATCH');
    }

    static generateInvoice(clientId, amount) {
        return this.request(`/invoices/generate?clientId=${clientId}&amount=${amount}`, 'POST');
    }

    static markInvoicePaid(invoiceId) {
        return this.request(`/invoices/${invoiceId}/pay`, 'PATCH');
    }
}
