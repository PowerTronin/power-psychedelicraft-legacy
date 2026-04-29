package ivorius.psychedelicraft.client.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LaboratoryJournalScreen extends Screen {
    private static final List<Page> PAGES = List.of(
            Page.of("overview"),
            Page.of("hallucinations"),
            Page.of("plants"),
            Page.of("greenhouse"),
            Page.of("drying"),
            Page.of("mash_tub"),
            Page.of("barrels"),
            Page.of("distillery"),
            Page.of("pump"),
            Page.of("villagers"),
            Page.of("safety")
    );

    private int pageIndex;
    private ButtonWidget previousButton;
    private ButtonWidget nextButton;

    public LaboratoryJournalScreen() {
        super(Text.translatable("gui.psychedelicraft.laboratory_journal.title"));
    }

    @Override
    protected void init() {
        int buttonY = height - 30;
        previousButton = addDrawableChild(ButtonWidget.builder(Text.translatable("gui.psychedelicraft.laboratory_journal.previous"), button -> previousPage())
                .dimensions(width / 2 - 155, buttonY, 70, 20)
                .build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
                .dimensions(width / 2 - 40, buttonY, 80, 20)
                .build());
        nextButton = addDrawableChild(ButtonWidget.builder(Text.translatable("gui.psychedelicraft.laboratory_journal.next"), button -> nextPage())
                .dimensions(width / 2 + 85, buttonY, 70, 20)
                .build());
        updateButtons();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int panelWidth = Math.min(340, width - 32);
        int panelHeight = Math.min(230, height - 72);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2 - 8;
        int right = left + panelWidth;
        int bottom = top + panelHeight;

        context.fill(left - 5, top - 5, right + 5, bottom + 5, 0xAA1D140C);
        context.fill(left, top, right, bottom, 0xFFF1DFC0);
        context.drawBorder(left, top, panelWidth, panelHeight, 0xFF5A3722);
        context.drawBorder(left + 4, top + 4, panelWidth - 8, panelHeight - 8, 0x66945F38);

        Page page = PAGES.get(pageIndex);
        context.drawCenteredTextWithShadow(textRenderer, getTitle(), width / 2, top + 12, 0xFFF6E9C8);
        context.drawText(textRenderer, Text.translatable(page.titleKey()).formatted(Formatting.BOLD), left + 18, top + 32, 0xFF2E2116, false);

        int y = top + 50;
        int textWidth = panelWidth - 36;
        String[] paragraphs = Text.translatable(page.bodyKey()).getString().split("\\n\\n");
        for (String paragraph : paragraphs) {
            List<OrderedText> lines = textRenderer.wrapLines(Text.literal(paragraph), textWidth);
            for (OrderedText line : lines) {
                if (y > bottom - 24) {
                    break;
                }
                context.drawText(textRenderer, line, left + 18, y, 0xFF2E2116, false);
                y += 10;
            }
            y += 4;
            if (y > bottom - 24) {
                break;
            }
        }

        Text pageCounter = Text.translatable("gui.psychedelicraft.laboratory_journal.page", pageIndex + 1, PAGES.size());
        context.drawCenteredTextWithShadow(textRenderer, pageCounter, width / 2, bottom - 16, 0xFF5A3722);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            previousPage();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            nextPage();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void previousPage() {
        if (pageIndex > 0) {
            pageIndex--;
            updateButtons();
        }
    }

    private void nextPage() {
        if (pageIndex < PAGES.size() - 1) {
            pageIndex++;
            updateButtons();
        }
    }

    private void updateButtons() {
        if (previousButton != null) {
            previousButton.active = pageIndex > 0;
        }
        if (nextButton != null) {
            nextButton.active = pageIndex < PAGES.size() - 1;
        }
    }

    private record Page(String titleKey, String bodyKey) {
        static Page of(String id) {
            String prefix = "gui.psychedelicraft.laboratory_journal." + id;
            return new Page(prefix + ".title", prefix + ".body");
        }
    }
}
