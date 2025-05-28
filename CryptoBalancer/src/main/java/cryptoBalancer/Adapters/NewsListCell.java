package cryptoBalancer.Adapters;

import cryptoBalancer.Models.Entities.News;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class NewsListCell extends ListCell<News> {
    @Override
    protected void updateItem(News item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            // Создаём вертикальный контейнер для всех элементов
            VBox vbox = new VBox(10); // Отступ между элементами 10px
            vbox.setPadding(new Insets(10)); // Внешние отступы вокруг ячейки
            vbox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID, null, new BorderWidths(1)))); // Граница ячейки

            // Первая строка: дата и гиперссылка
            HBox headerBox = new HBox(10); // Отступ между элементами в строке 10px
            Label dateLabel = new Label("Дата: " + item.getPublishedOn());
            Hyperlink link = new Hyperlink(item.getTitle());
            link.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(item.getUrl()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            headerBox.getChildren().addAll(dateLabel, link);

            // Описание с переносом текста
            Label descLabel = new Label("Описание: " + item.getDescription());
            descLabel.setWrapText(true); // Включаем перенос текста
            descLabel.setMaxWidth(500); // Ограничиваем ширину для переноса

            // Добавляем элементы в вертикальный контейнер
            vbox.getChildren().addAll(headerBox, descLabel);

            // Добавляем разделитель, если это не последняя ячейка в списке
            ListView<News> listView = getListView();
            if (listView != null && getIndex() < listView.getItems().size() - 1) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(5, 0, 0, 0)); // Отступ сверху для разделителя
                vbox.getChildren().add(separator);
            }

            setGraphic(vbox);
        }
    }
}
