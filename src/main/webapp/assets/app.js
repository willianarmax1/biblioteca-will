'use strict';
// O CRUD funciona sem JavaScript. Este aprimoramento evita o envio duplo por clique.
document.querySelectorAll('form[method="post"]').forEach(form => {
  form.addEventListener('submit', () => {
    const button = form.querySelector('button[type="submit"]');
    if (button) { button.disabled = true; button.textContent = 'Salvando…'; }
  });
});
