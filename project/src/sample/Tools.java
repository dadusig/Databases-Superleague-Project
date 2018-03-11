package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tools
{
    private static final String DB_URL = "jdbc:mysql://localhost/superleague?useSSL=false";
    //  Database credentials
    private static final String USER = "league_admin";
    private static final String PASS = "12345678";

    protected static Connection connectToDatabase() throws SQLException
    {
        return DriverManager.getConnection(DB_URL,USER,PASS);
    }


    public static void createTableViewFromQuery(TableView table, String query)
    {
        table.getColumns().clear();

        Connection con = null;

        ObservableList<ObservableList> data;
        data = FXCollections.observableArrayList();


        try
        {
            con = connectToDatabase();
            ResultSet rs = con.createStatement().executeQuery(query);

            //add columns
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnLabel(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                table.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }

            //add data
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added "+row );
                data.add(row);
            }

            table.setItems(data);


        }
        catch (Exception e)
        {
            System.out.println("Error on Building Data");
        }


    }
}
