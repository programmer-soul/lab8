package command;

import main.Commands;

/**
 * Команда 'exit'. Завершает выполнение.
 * @author Matvei Baranov
 */
public class Exit extends Command{
    private final Commands commands;
    public Exit(Commands commands)
    {
        super("exit","завершить программу (без сохранения в файл)");
        this.commands=commands;
    }
    @Override
    public boolean execute(String commandName,String parametr,boolean script) {
        if (parametr.isEmpty()){
            return true;
        }
        else
        {
            commands.setMessage("У этой команды не должно быть параметров!");
            return false;
        }
    }
}
