package command;
import main.Person;
import main.Commands;
/**
 * Команда 'replace_if_lower'. Заменить значение по ключу, если значение меньше заданного.
 * @author Matvei Baranov
 */
public class ReplaceIfLower extends Command{
    private final Commands commands;
    public ReplaceIfLower(Commands commands){
        super("replace_if_lower {id} {element}","заменить значение по ключу, если значение меньше заданного");
        this.commands=commands;
    }
    @Override
    public boolean execute(String commandName,String parametr,boolean script) {
        if (!parametr.isEmpty()){
            if (Person.validateID(parametr)){
                commands.insert(Integer.parseInt(parametr),script,true,true,false);
            }
            else{
                commands.setMessage("Ошибка ID");
            }
        }
        else{
            commands.setMessage("У этой команды обязательный параметр ID!");
        }
        return false;
    }
}
