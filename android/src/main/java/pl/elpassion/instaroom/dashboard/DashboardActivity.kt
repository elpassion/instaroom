package pl.elpassion.instaroom.dashboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.CHAIN_PACKED
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.lifecycle.ViewModelProviders
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.guideline
import pl.elpassion.instaroom.DI
import pl.elpassion.instaroom.R

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(constraintLayout {
            setTheme(R.style.ItemRoomStyle)
            val itemRoomNameTv = textView {
                backgroundResource = R.color.colorPrimary
                id = Ids.item_room_name_tv
                setPadding(dimen(R.dimen.medium_padding), dip(10), dimen(R.dimen.medium_padding), dip(10))
                textColor = android.R.color.white
                textSize = 12f
            }.lparams(width = matchParent, height = wrapContent)
            val itemRoomMeetingView = view {
                backgroundResource = R.color.greyDark
                id = Ids.item_room_meeting_v
            }.lparams(width = matchParent, height = dip(0))
            val itemRoomFreeIconIv = imageView(R.drawable.ic_android) {
                backgroundResource = R.drawable.round
                setBackgroundColor(getColor(R.color.blueDark))
                id = Ids.item_room_free_icon_iv
                padding = dip(8)
                visibility = View.INVISIBLE
            }.lparams(width = dip(36), height = dip(36))
            val itemRoomFreeTv = textView(R.string.this_room_is_free_now) {
                id = Ids.item_room_free_tv
                textColor = android.R.color.white
                visibility = View.INVISIBLE
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomMeetingTitleTv = textView {
                id = Ids.item_room_meeting_title_tv
                textColor = android.R.color.white
                textSize = 16f
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomMeetingTimeStartTv = textView {
                id = Ids.item_room_meeting_time_start_tv
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomMeetingTimeStartUnitTv = textView {
                id = Ids.item_room_meeting_time_start_unit_tv
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomMeetingTimeSeparator = view {
                backgroundResource = android.R.color.white
                id = Ids.item_room_meeting_time_separator
            }.lparams(width = dip(6), height = dip(0.8f))
            val itemRoomMeetingTimeEndTv = textView {
                id = Ids.item_room_meeting_time_end_tv
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomMeetingTimeEndUnitTv = textView {
                id = Ids.item_room_meeting_time_end_unit_tv
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomMeetingCalendarIcon = imageView(R.drawable.ic_android) {
                backgroundResource = R.drawable.round
                setBackgroundColor(getColor(android.R.color.white))
                padding = dip(10)
            }.lparams(width = dip(46), height = dip(46))
            val itemRoomLineV = view {
                backgroundResource = R.color.colorPrimary
                id = Ids.item_room_line_v
            }.lparams(width = matchParent, height = dip(1))
            val con = view {
                backgroundResource = R.color.greyDark
            }.lparams(width = matchParent, height = dip(0))
            val itemRoomArrowIconIv = imageView(R.drawable.ic_arrow_forward) {
                id = Ids.item_room_arrow_icon_iv
            }.lparams(width = dip(16), height = dip(16))
            val itemRoomTimeStartTv = textView {
                id = Ids.item_room_time_start_tv
                textColor = android.R.color.white
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomTimeStartUnitTv = textView {
                id = Ids.item_room_time_start_unit_tv
                textSize = 9f
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomTimeRangeIconIv = view {
                backgroundResource = android.R.color.white
                id = Ids.item_room_time_range_icon_iv
            }.lparams(width = dip(4), height = dip(0.7f))
            val itemRoomTimeEndTv = textView {
                id = Ids.item_room_time_end_tv
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomTimeEndUnitTv = textView {
                id = Ids.item_room_time_end_unit_tv
                textSize = 9f
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomNextMeetingTitleSeparator = view {
                backgroundResource = android.R.color.white
                id = Ids.item_room_next_meeting_title_separator
            }.lparams(width = dip(0.7f), height = dip(10))
            val itemRoomNextMeetingTitleTv = textView {
                id = Ids.item_room_next_meeting_title_tv
            }.lparams(width = wrapContent, height = wrapContent)
            val con2 = view {
                backgroundResource = R.color.colorPrimary
            }.lparams(width = matchParent, height = dip(0))
            val itemRoomStatusTv = textView {
                id = Ids.item_room_status_tv
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomStatusDescTv = textView {
                id = Ids.item_room_status_desc_tv
                setPadding(0, dimen(R.dimen.basic_padding), 0, dimen(R.dimen.basic_padding))
                setPadding(0, dimen(R.dimen.basic_padding), 0, dimen(R.dimen.basic_padding))
                textColor = android.R.color.white
            }.lparams(width = wrapContent, height = wrapContent)
            val itemRoomBookButton = button {
                id = Ids.item_room_book_button
            }.lparams(width = wrapContent, height = wrapContent)
            guideline {
                id = Ids.item_room_guideline_start
            }.lparams(width = wrapContent, height = matchParent) {
                orientation = ConstraintLayout.LayoutParams.VERTICAL
                guideBegin = R.dimen.medium_padding
            }
            guideline {
                id = Ids.item_room_guideline_end
            }.lparams(width = wrapContent, height = matchParent) {
                orientation = ConstraintLayout.LayoutParams.VERTICAL
                guideEnd = R.dimen.medium_padding
            }
            applyConstraintSet {
                itemRoomMeetingView {
                    connect(
                        BOTTOM to TOP of R.id.item_room_line_v,
                        TOP to BOTTOM of R.id.item_room_name_tv
                    )
                }
                itemRoomFreeIconIv {
                    connect(
                        BOTTOM to TOP of R.id.item_room_free_tv,
                        END to END of PARENT_ID,
                        START to START of PARENT_ID,
                        TOP to BOTTOM of R.id.item_room_name_tv
                    )
                }
                itemRoomFreeTv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_meeting_v,
                        END to END of R.id.item_room_free_icon_iv,
                        START to START of R.id.item_room_free_icon_iv,
                        TOP to BOTTOM of R.id.item_room_free_icon_iv
                    )
                }
                itemRoomMeetingTitleTv {
                    connect(
                        BOTTOM to TOP of R.id.item_room_meeting_time_start_tv,
                        START to START of R.id.item_room_guideline_start,
                        TOP to BOTTOM of R.id.item_room_name_tv
                    )
                    verticalChainStyle = CHAIN_PACKED
                }
                itemRoomMeetingTimeStartTv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_line_v,
                        START to START of R.id.item_room_guideline_start,
                        TOP to BOTTOM of R.id.item_room_meeting_title_tv
                    )
                }
                itemRoomMeetingTimeStartUnitTv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_meeting_time_start_tv,
                        START to END of R.id.item_room_meeting_time_start_tv
                    )
                }
                itemRoomMeetingTimeSeparator {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_meeting_time_start_tv,
                        START to END of R.id.item_room_meeting_time_start_unit_tv,
                        TOP to TOP of R.id.item_room_meeting_time_start_tv
                    )
                }
                itemRoomMeetingTimeEndTv {
                    connect(
                        BASELINE to BASELINE of R.id.item_room_meeting_time_start_tv,
                        START to END of R.id.item_room_meeting_time_separator
                    )
                }
                itemRoomMeetingTimeEndUnitTv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_meeting_time_start_tv,
                        END to END of R.id.item_room_guideline_end,
                        TOP to TOP of R.id.item_room_meeting_title_tv
                    )
                }

                itemRoomMeetingCalendarIcon {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_meeting_time_start_tv,
                        END to END of R.id.item_room_guideline_end,
                        TOP to TOP of R.id.item_room_meeting_title_tv
                    )
                }

                itemRoomLineV {
                    connect(
                        TOP to BOTTOM of R.id.item_room_meeting_v
                    )
                }
                con {
                    connect(
                        BOTTOM to TOP of R.id.item_room_status_desc_tv,
                        TOP to BOTTOM of R.id.item_room_line_v
                    )
                }
                itemRoomArrowIconIv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_time_start_tv,
                        START to START of R.id.item_room_guideline_start,
                        TOP to BOTTOM of R.id.item_room_line_v,
                        TOP to TOP of R.id.item_room_time_start_tv
                    )
                }
                itemRoomTimeStartTv {
                    connect(
                        START to END of R.id.item_room_arrow_icon_iv,
                        TOP to BOTTOM of R.id.item_room_line_v
                    )
                }
                itemRoomTimeStartUnitTv {
                    connect(
                        BASELINE to BASELINE of R.id.item_room_time_start_tv,
                        START to END of R.id.item_room_time_start_tv,
                        TOP to TOP of R.id.item_room_time_start_tv
                    )
                }
                itemRoomTimeRangeIconIv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_time_start_tv,
                        START to END of R.id.item_room_time_start_unit_tv,
                        TOP to TOP of R.id.item_room_time_start_tv
                    )
                }
                itemRoomTimeEndTv {
                    connect(
                        START to END of R.id.item_room_time_range_icon_iv,
                        TOP to BOTTOM of R.id.item_room_line_v
                    )
                }
                itemRoomTimeEndUnitTv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_time_end_tv,
                        START to END of R.id.item_room_time_end_tv,
                        TOP to TOP of R.id.item_room_time_end_tv
                    )
                }
                itemRoomNextMeetingTitleSeparator {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_time_start_tv,
                        START to END of R.id.item_room_time_end_unit_tv,
                        TOP to TOP of R.id.item_room_time_start_tv
                    )
                }
                itemRoomNextMeetingTitleTv {
                    connect(
                        BASELINE to BASELINE of R.id.item_room_time_start_tv,
                        START to END of R.id.item_room_next_meeting_title_separator
                    )
                }
                con2 {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_status_desc_tv,
                        TOP to TOP of R.id.item_room_status_desc_tv
                    )
                }
                itemRoomStatusTv {
                    connect(
                        BOTTOM to BOTTOM of R.id.item_room_status_desc_tv,
                        START to START of R.id.item_room_guideline_start,
                        TOP to TOP of R.id.item_room_status_desc_tv
                    )
                }
                itemRoomStatusDescTv {
                    connect(
                        START to END of R.id.item_room_status_tv,
                        TOP to BOTTOM of R.id.item_room_time_start_tv
                    )
                }
                itemRoomBookButton {
                    connect(
                        TOP to BOTTOM of R.id.item_room_status_desc_tv
                    )
                }
            }
        })
        val model = ViewModelProviders.of(this, DashboardViewModelFactory(DI.provideInstaRoomApi()))
            .get(DashboardViewModel::class.java)
    }

    private object Ids {
        val item_room_arrow_icon_iv = 1
        val item_room_book_button = 2
        val item_room_free_icon_iv = 3
        val item_room_free_tv = 4
        val item_room_guideline_end = 5
        val item_room_guideline_start = 6
        val item_room_line_v = 7
        val item_room_meeting_time_end_tv = 8
        val item_room_meeting_time_end_unit_tv = 9
        val item_room_meeting_time_separator = 10
        val item_room_meeting_time_start_tv = 11
        val item_room_meeting_time_start_unit_tv = 12
        val item_room_meeting_title_tv = 13
        val item_room_meeting_v = 14
        val item_room_name_tv = 15
        val item_room_next_meeting_title_separator = 16
        val item_room_next_meeting_title_tv = 17
        val item_room_status_desc_tv = 18
        val item_room_status_tv = 19
        val item_room_time_end_tv = 20
        val item_room_time_end_unit_tv = 21
        val item_room_time_range_icon_iv = 22
        val item_room_time_start_tv = 23
        val item_room_time_start_unit_tv = 24
    }
}