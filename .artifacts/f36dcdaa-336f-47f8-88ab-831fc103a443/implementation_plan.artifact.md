# Piano di Ristrutturazione Modulo Obiettivi e Salvadanaio Virtuale

## Panoramica
Riprogettazione completa del modulo obiettivi e risparmi virtuali per renderlo pulito, disaccoppiato dalle transazioni reali e aderente alle specifiche richieste.

## Modifiche Principali
1. **Eliminazione Transazioni Fittizie**: Rimozione totale dei tipi `VirtualSaving` e `VirtualWithdrawal` dal registro contabile per evitare crash al click sulle transazioni.
2. **Struttura Obiettivi Ridisegnata**:
   - Salvadanaio Virtuale in primo piano con saldo totale accantonato e ripartizione per conto (solo conti con accantonamenti > 0) con barre percentuali.
   - Pulsanti **Accantona** e **Svincola** (quest'ultimo con slider percentuale bidirezionale e campo numerico in tempo reale).
   - Obiettivi individuali associati a un conto specifico, con validazione emoji rigorosa (default 🐷), scadenza opzionale e target.
   - Pulsante a forma di moneta al raggiungimento del 100% del target, che apre il popup per registrare l'acquisto reale come spesa e decurtare l'accantonamento.
   - Visualizzazione del saldo effettivo decurtato dell'accantonamento nella Home e indicazione del totale accantonato.
