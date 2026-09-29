document.addEventListener('DOMContentLoaded', () => {
    const auditForm = document.getElementById('public-audit-form');
    const responseBox = document.getElementById('form-response');

    if (auditForm) {
        auditForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const submitBtn = auditForm.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerText = 'Submitting Request...';

            const payload = {
                businessName: document.getElementById('businessName').value,
                contactPerson: document.getElementById('contactPerson').value,
                email: document.getElementById('email').value,
                phoneNumber: document.getElementById('phoneNumber').value,
                physicalAddress: document.getElementById('physicalAddress').value
            };

            try {
                const newClient = await OmnicareAPI.registerClient(payload);
                responseBox.className = 'response-message success';
                responseBox.innerText = `Success! Audit request registered for ${newClient.businessName}. Our technician will reach out shortly.`;
                auditForm.reset();
            } catch (error) {
                responseBox.className = 'response-message error';
                responseBox.innerText = `Registration failed: ${error.message}`;
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerText = 'Submit Audit Request';
            }
        });
    }
});
