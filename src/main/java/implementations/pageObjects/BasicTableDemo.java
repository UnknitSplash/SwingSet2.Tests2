package implementations.pageObjects;

import abstracts.PageObject;
import implementations.helpers.HeaderDragAndDrop;
import implementations.helpers.ImageHelper;
import implementations.wrappers.Lazy;
import interfaces.pageObjects.TableDemo;
import interfaces.pageObjects.View;
import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.*;
import org.junit.Assert;
import utils.ResourceManager;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BasicTableDemo extends PageObject implements TableDemo {

    public BasicTableDemo(FrameFixture frame, View view) {
        super(frame, view);
    }

    //region Helpers
    protected HeaderDragAndDrop dragAndDrop = new HeaderDragAndDrop(frame.robot);
    protected ImageHelper imageHelper = new ImageHelper();
    //endregion

    //region Expected Texts
    private final String reorderingText = ResourceManager.getResString("TableDemo.reordering_allowed");
    private final String horzText = ResourceManager.getResString("TableDemo.horz_lines");
    private final String vertText = ResourceManager.getResString("TableDemo.vert_lines");
    private final String columnSelText = ResourceManager.getResString("TableDemo.column_selection");
    private final String rowSelText = ResourceManager.getResString("TableDemo.row_selection");
    private final String intercellColonText = ResourceManager.getResString("TableDemo.intercell_spacing_colon");
    private final String rowHeightColonText = ResourceManager.getResString("TableDemo.row_height_colon");
    private final String intercellSliderName = ResourceManager.getResString("TableDemo.intercell_spacing");
    private final String rowHeightSliderName = ResourceManager.getResString("TableDemo.row_height");
    private final String selectionDefaultValue = ResourceManager.getResString("TableDemo.multiple_ranges");
    private final String resizeDefaultValue = ResourceManager.getResString("TableDemo.subsequent_columns");
    //endregion

    //region Components
    protected Lazy<JTableFixture> tableView = wait.lazy(() -> frame.table());
    protected Lazy<JScrollPaneFixture> scrollpane = wait.lazy(() -> frame.scrollPane());

    protected Lazy<JCheckBoxFixture> isColumnReorderingAllowedCheckBox = wait.lazy(() -> frame.checkBox(getCheckBoxTextMatcher(reorderingText)));
    protected Lazy<JCheckBoxFixture> showHorizontalLinesCheckBox = wait.lazy(() -> frame.checkBox(getCheckBoxTextMatcher(horzText)));
    protected Lazy<JCheckBoxFixture> showVerticalLinesCheckBox = wait.lazy(() -> frame.checkBox(getCheckBoxTextMatcher(vertText)));

    protected Lazy<JCheckBoxFixture> isColumnSelectionAllowedCheckBox = wait.lazy(() -> frame.checkBox(getCheckBoxTextMatcher(columnSelText)));
    protected Lazy<JCheckBoxFixture> isRowSelectionAllowedCheckBox = wait.lazy(() -> frame.checkBox(getCheckBoxTextMatcher(rowSelText)));

    protected Lazy<JLabelFixture> interCellSpacingLabel = wait.lazy(() -> frame.label(getLabelTextMatcher(intercellColonText)));
    protected Lazy<JLabelFixture> rowHeightLabel = wait.lazy(() -> frame.label(getLabelTextMatcher(rowHeightColonText)));

    protected Lazy<JSliderFixture> interCellSpacingSlider = wait.lazy(() -> frame.slider(getSliderAccessNameMatcher(intercellSliderName)));
    protected Lazy<JSliderFixture> rowHeightSlider = wait.lazy(() -> frame.slider(getSliderAccessNameMatcher(rowHeightSliderName)));

    protected Lazy<JComboBoxFixture> selectionModeComboBox = wait.lazy(() -> frame.comboBox(getComboBoxSelectedMatcher(selectionDefaultValue)));
    protected Lazy<JComboBoxFixture> resizeModeComboBox = wait.lazy(() -> frame.comboBox(getComboBoxSelectedMatcher(resizeDefaultValue)));
    //endregion

    //TODO remove this temporary sandbox code
    @Override
    public void clickTable() {
        JTableFixture table = tableView.get();
//        table.click();
//        table.drag(TableCell.row(0).column(0));
//        table.drop(TableCell.row(0).column(3));
//        table.tableHeader().clickColumn(0);
//
//        scrollpane.get().verticalScrollBar().scrollBlockDown(6);
//        isColumnReorderingAllowedCheckBox.get().uncheck();
//        isColumnReorderingAllowedCheckBox.get().click();
//        showHorizontalLinesCheckBox.get().click();
//        showVerticalLinesCheckBox.get().click();
//        interCellSpacingLabel.get().click();
//        rowHeightLabel.get().click();
//        isColumnReorderingAllowedCheckBox.get().click();
//        isColumnSelectionAllowedCheckBox.get().click();
//        isRowSelectionAllowedCheckBox.get().click();
//        interCellSpacingSlider.get().slideToMaximum();
//        rowHeightSlider.get().slideToMinimum();
//        selectionModeComboBox.get().selectItem(0);
//        resizeModeComboBox.get().selectItem(0);
        JTableHeader header = table.tableHeader().target;
        Point from = dragAndDrop.pointAtName(header, dragAndDrop.exactText("First Name"));
        Point to = dragAndDrop.pointAtName(header, dragAndDrop.containsText("Movie"));
        dragAndDrop.drag(header, from);
        dragAndDrop.drop(header,to);

        Assert.assertTrue(table.cell("Mike").column == 2);

        Point b1 = dragAndDrop.rightBorder(header, dragAndDrop.containsText("First Name"));
        Point to2 = dragAndDrop.pointAtName(header, dragAndDrop.containsText("Movie"));
        dragAndDrop.dragAndDrop(header, b1, to2);

        Point b2 = dragAndDrop.leftBorder(header, dragAndDrop.containsText("Movie"));
        Point to3 = dragAndDrop.pointAtName(header, dragAndDrop.containsText("Last"));
        dragAndDrop.dragAndDrop(header, b2, to3);

        Dimension originalSpacing = table.target.getIntercellSpacing();
        interCellSpacingSlider.get().slideToMaximum();
        Dimension newSpacing = table.target.getIntercellSpacing();
        Assert.assertTrue(originalSpacing.height < newSpacing.height && originalSpacing.width < newSpacing.width);

        interCellSpacingSlider.get().slideToMinimum();
        newSpacing = table.target.getIntercellSpacing();
        Assert.assertTrue(originalSpacing.height > newSpacing.height && originalSpacing.width > newSpacing.width);

        Integer originalHeight = table.target.getRowHeight();
        rowHeightSlider.get().slideToMaximum();
        Integer newHeight = table.target.getRowHeight();
        Assert.assertTrue(originalHeight < newHeight);

        originalHeight = table.target.getRowHeight();
        rowHeightSlider.get().slideToMinimum();
        newHeight = table.target.getRowHeight();
        Assert.assertTrue(originalHeight > newHeight);

        String value = table.valueAt(TableCell.row(0).column(5));
        Assert.assertTrue(value.isEmpty());

        BufferedImage expected = imageHelper.loadBufferedImageFromRes("images/ImageClub/food/strawberry.jpg");
        int imageType = expected.getType();
        ImageIcon image = (ImageIcon)table.target.getValueAt(0, 5);

        BufferedImage bimage = imageHelper.convertToBufferedImage(image.getImage(), imageType);
        int bh = bimage.getHeight();
        int bw = bimage.getWidth();
        int eh = expected.getHeight();
        int ew = expected.getWidth();
        Assert.assertEquals(bh, eh);
        Assert.assertEquals(bw, ew);
        Assert.assertTrue(imageHelper.imagesAreEqual(bimage, expected));
    }

    @Override
    public int getIntercellSpacingMax() {
        return interCellSpacingSlider.get().target.getMaximum();
    }

    @Override
    public int getIntercellSpacingMin() {
        return interCellSpacingSlider.get().target.getMinimum();
    }

    @Override
    public int getIntercellSpacingValue() {
        return interCellSpacingSlider.get().target.getValue();
    }

    @Override
    public int setIntercellSpacingToMax() {
        JSliderFixture slider = interCellSpacingSlider.get().slideToMaximum();
        return slider.target.getValue();
    }

    @Override
    public int setIntercellSpacingToMin() {
        JSliderFixture slider = interCellSpacingSlider.get().slideToMinimum();
        return slider.target.getValue();
    }

    @Override
    public int setIntercellSpacingTo(int value) {
        JSliderFixture slider = interCellSpacingSlider.get().slideTo(value);
        return slider.target.getValue();
    }

    @Override
    public Dimension getTableIntercellSpacing() {
        return tableView.get().target.getIntercellSpacing();
    }

    @Override
    public int getRowHeightMax() {
        return rowHeightSlider.get().target.getMaximum();
    }

    @Override
    public int getRowHeightMin() {
        return rowHeightSlider.get().target.getMinimum();
    }

    @Override
    public int setRowHeightToMax() {
        JSliderFixture slider = rowHeightSlider.get().slideToMaximum();
        return slider.target.getValue();
    }

    @Override
    public int getTableRowHeight() {
        return tableView.get().target.getRowHeight();
    }

    @Override
    public int setRowHeightToMin() {
        JSliderFixture slider = rowHeightSlider.get().slideToMinimum();
        return slider.target.getValue();
    }

    @Override
    public int setRowHeightTo(int value){
        JSliderFixture slider = rowHeightSlider.get().slideTo(value);
        return slider.target.getValue();
    }
}
