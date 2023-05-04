import styled from "@emotion/styled";
import { Grid, TextField, Rating } from "@mui/material";
import { useRef, useState } from "react";

const ReviewInput = (props: any) => {
  const [rating, setRating] = useState(2);
  const inputRef = useRef<HTMLInputElement>();

  function submitHandler(e: any) {
    e.preventDefault();
    if (inputRef.current) {
      //여기 useMutation 작성해야함
      console.log({
        rating,
        content: inputRef.current.value,
      });
    }
  }
  return (
    <Background>
      <form onSubmit={submitHandler}>
        <Grid container alignItems={"center"}>
          <Grid item xs={9}>
            <TextField
              fullWidth
              id="standard-basic"
              label="리뷰작성"
              variant="outlined"
              inputRef={inputRef}
            />
          </Grid>
          <Grid item xs={3}>
            <Rating
              size="small"
              name="simple-controlled"
              value={rating}
              onChange={(event, newValue) => {
                setRating(newValue ? newValue : 0);
              }}
              style={{

                border: "1px solid #dddddd",
                boxSizing: "border-box",
                padding: "18px 0px 18px 10px",
                width:"100%",
                textAlign:"center",
                borderRadius: "5px",
              }}
            />
          </Grid>
        </Grid>
      </form>
    </Background>
  );
};

//여기는 SeoulNamsan 적용 안되서 기본 sens-self로 함
const Background = styled.div`

`;

export default ReviewInput;
