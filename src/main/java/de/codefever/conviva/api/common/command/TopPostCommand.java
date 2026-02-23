package de.codefever.conviva.api.common.command;

import de.codefever.conviva.model.general.Message;

import java.util.List;

/**
 * Command to show the all-time top post.
 */
public class TopPostCommand implements BotCommand {
    @Override
    public String command() {
        return "!toppost";
    }

    @Override
    public String description() {
        return "Zeigt den All-Time-Top-Post an.";
    }

    @Override
    public String outputIdentifier() {
        return "###TOP-POST###";
    }

    @Override
    public String run(final Message callToCommand, final List<Message> messages) {

        if (this.isIntendedForQuotedMessage(callToCommand)) {
            return "";
        }

        return "Habe der Polizei auf dem Revier die Lage ganz sachlich geschildert. Dann hat mich eine Frau, die arbeitet, genauer befragt. Dann habe ich ihren Kollegen angeguckt und gesagt: \"Sag mal, ist das normal, dass die Frauen bei Ihnen die Hosen anhaben?\" und \"Ich rede nicht mit einer Hausfrau\", das hat gesessen. Da hatte die Alte erstmal zu schlucken (jetzt als Redewendung) und hat dann keine dummen Fragen mehr gestellt. Der (doch sehr schlaksige) Polizist meinte dann irgendwas von wegen \"das kannst du doch nicht sagen\" und \"du bist ein Sexist\" - doch bevor er überhaupt den Satz ausgesprochen hat, habe ich gesagt \"Halt! Stop! Von Ihnen lasse ich mich ganz bestimmt nicht duzen, Herr Wachtmeister, das ist ein Verfahrensfehler.\" Haha, da ist meine Provokationstaktik perfekt aufgegangen und die Idioten mussten aufgrund des Fehlers die Anklage fallen lassen. Habe dann noch bei ihnen in den Flur gepisst. Da ist dem Typen die Hutschnur geplatzt und er hat losgeplärrt. Habe ich gesagt \"Ist doch nur eine Ordnungswidrigkeit, das interessiert mich nicht. Hier ist ihr Geld\" und habe ihm 20 Euro in die Pfütze geworfen. Sein Kollege musste grinsen. Dann bin ich erstmal schön über die rote Ampel gebrettert und bin jetzt wieder zuhause und schaue den Handwerkern bei der ARbeit zu.";
    }

    @Override
    public String beforeMessage() {
        return "";
    }

    @Override
    public String afterMessage() {
        return "Der Post liegt zu weit in der Vergangenheit für mich. Daher für mehr Infos dieses Top-Posts bitte bei Matze melden.";
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isRunInThread() {
        return true;
    }

}
