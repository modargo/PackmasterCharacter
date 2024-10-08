package thePackmaster.vfx.womaninbluepack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public class PotionThrowEffect extends AbstractGameEffect {
    private final Texture img;
    private final float sX;
    private final float sY;
    private float cX;
    private float cY;
    private final float dX;
    private final float dY;
    private float yOffset;
    private final float bounceHeight;
    private boolean playedSfx = false;
    private boolean mute = false;

    private final ArrayList<Vector2> previousPos = new ArrayList<>();

    public PotionThrowEffect(String imgSrc, float srcX, float srcY, float destX, float destY, float scale, float duration, boolean mute, boolean bigHeight) {

        img = ImageMaster.loadImage(imgSrc);

        this.sX = srcX + 40;
        this.sY = srcY - 40;
        this.cX = this.sX;
        this.cY = this.sY;
        this.dX = destX;
        this.dY = destY;
        this.scale = scale;
        this.rotation = 0.0F;
        this.mute = mute;
        this.duration = duration;
        this.color = new Color(1, 1.0F, 1, 0.0F);

        float height = 100f;
        if (bigHeight) height = 300F;

        if (this.sY > this.dY) {
            this.bounceHeight = height * Settings.scale;
        } else {
            this.bounceHeight = this.dY - this.sY + height * Settings.scale;
        }

    }

    public void update() {
        if (!this.playedSfx && !this.mute) {
            this.playedSfx = true;
            String sound = "POTION_1";
            switch (AbstractDungeon.cardRng.random(0, 2)) {
                case 0:
                    break;
                case 1:
                    sound = "POTION_2"; break;
                case 2:
                    sound = "POTION_3"; break;
            }

            CardCrawlGame.sound.playA(sound, MathUtils.random(-0.1F, 0.1F));


        }

        this.cX = Interpolation.linear.apply(this.dX, this.sX, this.duration / 0.6F);
        this.cY = Interpolation.linear.apply(this.dY, this.sY, this.duration / 0.6F);
        this.previousPos.add(new Vector2(this.cX + MathUtils.random(-30.0F, 30.0F) * Settings.scale, this.cY + this.yOffset + MathUtils.random(-30.0F, 30.0F) * Settings.scale));
        if (this.previousPos.size() > 20) {
            this.previousPos.remove(this.previousPos.get(0));
        }

        if (this.dX > this.sX) {
            this.rotation -= Gdx.graphics.getDeltaTime() * 300.0F;
        } else {
            this.rotation += Gdx.graphics.getDeltaTime() * 300.0F;
        }

        if (this.duration > 0.3F) {
            this.color.a = Interpolation.exp5In.apply(1.0F, 0.5F, (this.duration - 0.3F) / 0.3F) * Settings.scale;
            this.yOffset = Interpolation.circleIn.apply(this.bounceHeight, 0.0F, (this.duration - 0.3F) / 0.3F) * Settings.scale;
        } else {
            this.yOffset = Interpolation.circleOut.apply(0.0F, this.bounceHeight, this.duration / 0.3F) * Settings.scale;
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {

            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {

        sb.setColor(Color.BLACK);

        sb.draw(img, this.cX - (float) (img.getWidth() / 2), this.cY - (float) (img.getHeight() / 2) + this.yOffset, (float) img.getWidth() / 2.0F, (float) img.getHeight() / 2.0F, (float) img.getWidth(), (float) img.getHeight(), this.scale, this.scale, this.rotation, 0, 0, 100, 100, false, false);

        sb.setColor(new Color(1F, 1F, 1.0F, 100F));

        sb.draw(img, this.cX - (float) (img.getWidth() / 2), this.cY - (float) (img.getHeight() / 2) + this.yOffset, (float) img.getWidth() / 2.0F, (float) img.getHeight() / 2.0F, (float) img.getWidth(), (float) img.getHeight(), this.scale, this.scale, this.rotation, 0, 0, 100, 100, false, false);
        //sb.draw(img, this.cX - (float)(img.getWidth() / 2), this.cY - (float)(img.getHeight() / 2) + this.yOffset, (float)img.getWidth() / 2.0F, (float)img.getHeight() / 2.0F, (float)img.getWidth(), (float)img.getHeight(), this.scale, this.scale, this.rotation, 0, 0, 38, 38, false, false);

    }

    public void dispose() {
        this.img.dispose();
        this.isDone = true;
    }
}
