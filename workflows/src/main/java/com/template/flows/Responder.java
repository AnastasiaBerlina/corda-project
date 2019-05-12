package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.GradeState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.ReceiveFinalityFlow;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ******************
// * Responder flow *
// ******************
@InitiatedBy(Initiator.class)
public class Responder extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public Responder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        SignTransactionFlow signTransactionFlow = new SignTransactionFlow(counterpartySession) {

            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                requireThat(require -> {
                    require.using("There cant be inputs", stx.getInputs().size() == 0);
                    return null;
                });
            }
        };
        SecureHash id = subFlow(signTransactionFlow).getId();
        subFlow(new ReceiveFinalityFlow(counterpartySession, id));
        return null;
    }
}
