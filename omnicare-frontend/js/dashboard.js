document.addEventListener('DOMContentLoaded', () => {
    setupTabNavigation();
    loadClients();
    setupTicketForm();

    const invoiceForm = document.getElementById('generate-invoice-form');
    if (invoiceForm) {
        invoiceForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const payload = {
                clientId: Number(document.getElementById('invoiceClientId').value),
                amount: document.getElementById('invoiceAmount').value
            };

            try {
                const invoice = await OmnicareAPI.generateInvoice(payload.clientId, payload.amount);
                alert(`Invoice ${invoice.invoiceNumber} created successfully.`);
                invoiceForm.reset();
                renderBillingList();
            } catch (error) {
                alert(error.message || 'Unable to create invoice.');
            }
        });
    }

    renderBillingList();
});

function setupTabNavigation() {
    const tabs = document.querySelectorAll('.sidebar-menu li');
    tabs.forEach((tab) => {
        tab.addEventListener('click', () => {
            tabs.forEach((item) => item.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach((content) => content.classList.remove('active'));

            tab.classList.add('active');
            const targetTab = tab.getAttribute('data-tab');
            document.getElementById(`tab-${targetTab}`)?.classList.add('active');
        });
    });
}

function setupTicketForm() {
    const form = document.getElementById('create-ticket-form');
    if (!form) return;

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const clientId = Number.parseInt(document.getElementById('ticketClientId').value, 10);
        const payload = {
            clientId,
            priority: document.getElementById('ticketPriority').value,
            title: document.getElementById('ticketTitle').value.trim(),
            description: document.getElementById('ticketDescription').value.trim()
        };

        try {
            await OmnicareAPI.createTicket(payload);
            alert('Support ticket opened successfully!');
            form.reset();
            await loadClientTickets(clientId);
        } catch (error) {
            alert(`Error opening ticket: ${error.message}`);
        }
    });
}

async function loadClients() {
    const tableBody = document.getElementById('clients-table-body');
    if (!tableBody) return;

    tableBody.innerHTML = '<tr><td colspan="7">Loading clients...</td></tr>';

    try {
        const clients = await OmnicareAPI.getClients();

        if (!clients.length) {
            tableBody.innerHTML = '<tr><td colspan="7">No clients registered yet.</td></tr>';
            return;
        }

        tableBody.innerHTML = clients.map((client) => `
            <tr>
                <td>${client.id}</td>
                <td>${client.businessName}</td>
                <td>${client.contactPerson}</td>
                <td>${client.email}</td>
                <td>${client.phoneNumber}</td>
                <td>
                    <span class="status-badge ${String(client.status).toLowerCase()}">${client.status}</span>
                </td>
                <td>
                    <select class="status-select" data-client-id="${client.id}">
                        ${['PROSPECT', 'ONBOARDING', 'ACTIVE_RETAINER', 'SUSPENDED', 'TERMINATED']
                            .map((status) => `
                                <option value="${status}" ${status === client.status ? 'selected' : ''}>${status}</option>
                            `)
                            .join('')}
                    </select>
                    <button class="btn-action" data-client-id="${client.id}" data-action="tickets">Tickets</button>
                    <button class="btn-action" onclick="generateBilling(${client.id})">Bill Retainer</button>
                </td>
            </tr>
        `).join('');

        document.querySelectorAll('.status-select').forEach((select) => {
            select.addEventListener('change', async (event) => {
                const clientId = Number(event.target.dataset.clientId);
                const status = event.target.value;

                try {
                    await OmnicareAPI.updateClientStatus(clientId, status);
                    await loadClients();
                    renderBillingList();
                } catch (error) {
                    alert(error.message || 'Unable to update client status.');
                }
            });
        });

        document.querySelectorAll('[data-action="tickets"]').forEach((button) => {
            button.addEventListener('click', async () => {
                const clientId = Number(button.dataset.clientId);
                await loadClientTickets(clientId);
                document.querySelector('[data-tab="tickets"]').click();
            });
        });

        renderBillingList();
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="7">Unable to load clients: ${error.message}</td></tr>`;
    }
}

async function generateBilling(clientId) {
    const amount = prompt('Enter Monthly Retainer Amount (KES):', '15000');
    if (!amount) return;

    const parsedAmount = Number.parseFloat(amount);
    if (!Number.isFinite(parsedAmount) || parsedAmount <= 0) {
        alert('Enter a valid retainer amount greater than zero.');
        return;
    }

    try {
        const invoice = await OmnicareAPI.generateInvoice(clientId, parsedAmount);
        alert(`Invoice ${invoice.invoiceNumber} generated successfully for KES ${invoice.amount}!`);
    } catch (error) {
        alert(`Billing failed: ${error.message}`);
    }
}

async function loadClientTickets(clientId) {
    const ticketList = document.getElementById('ticket-list');
    if (!ticketList) return;

    ticketList.innerHTML = '<div class="empty-state">Loading tickets...</div>';

    try {
        const tickets = await OmnicareAPI.getClientTickets(clientId);

        if (!tickets.length) {
            ticketList.innerHTML = '<div class="empty-state">No tickets found for this client.</div>';
            return;
        }

        ticketList.innerHTML = tickets.map((ticket) => `
            <div class="info-card">
                <h4>${ticket.title}</h4>
                <p><strong>ID:</strong> ${ticket.id}</p>
                <p><strong>Priority:</strong> <span class="status-badge ${String(ticket.priority).toLowerCase()}">${ticket.priority}</span></p>
                <p><strong>Status:</strong> <span class="status-badge ${String(ticket.status).toLowerCase()}">${ticket.status}</span></p>
                <p>${ticket.description}</p>
            </div>
        `).join('');
    } catch (error) {
        ticketList.innerHTML = `<div class="empty-state">Unable to load tickets: ${error.message}</div>`;
    }
}

async function renderBillingList() {
    const billingList = document.getElementById('billing-list');
    if (!billingList) return;

    try {
        const clients = await OmnicareAPI.getClients();

        if (!clients.length) {
            billingList.innerHTML = '<div class="empty-state">No billing records available yet.</div>';
            return;
        }

        billingList.innerHTML = clients.map((client) => `
            <div class="info-card">
                <h4>${client.businessName}</h4>
                <p><strong>Client ID:</strong> ${client.id}</p>
                <p><strong>Primary Contact:</strong> ${client.contactPerson}</p>
                <p><strong>Status:</strong> <span class="status-badge ${String(client.status).toLowerCase()}">${client.status}</span></p>
                <div class="form-group">
                    <label>Retainer Amount (KES)</label>
                    <input type="number" min="0" step="0.01" value="15000" data-client-id="${client.id}" class="invoice-amount-input" />
                </div>
                <button class="btn-action generate-invoice-btn" data-client-id="${client.id}">Generate Invoice</button>
            </div>
        `).join('');

        document.querySelectorAll('.generate-invoice-btn').forEach((button) => {
            button.addEventListener('click', async () => {
                const clientId = Number(button.dataset.clientId);
                const amountInput = document.querySelector(`.invoice-amount-input[data-client-id="${clientId}"]`);
                const amount = amountInput ? amountInput.value : '0';

                try {
                    const invoice = await OmnicareAPI.generateInvoice(clientId, amount);
                    alert(`Invoice ${invoice.invoiceNumber} generated successfully.`);
                } catch (error) {
                    alert(error.message || 'Could not generate invoice.');
                }
            });
        });
    } catch (error) {
        billingList.innerHTML = `<div class="empty-state">Unable to load billing info: ${error.message}</div>`;
    }
}
