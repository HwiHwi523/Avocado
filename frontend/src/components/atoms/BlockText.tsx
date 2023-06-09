import styled from "@emotion/styled";
type Style = {
  type?: string;
  size?: string;
  fontWeight?: number;
  color?: string;
  style?: React.CSSProperties;
};

const BlockText = ({
  type = "M",
  size = "1rem",
  fontWeight = 400,
  color = "black",
  children,
  style = {},
}: React.PropsWithChildren<Style>) => {
  return (
    <StyledP
      type={type}
      size={size}
      fontWeight={fontWeight}
      color={color}
      style={style}
    >
      {children}
    </StyledP>
  );
};

const fontFam = `SeoulNamsan${(props: any) => props.type}`;

const StyledP = styled.div<Style>`
  font-family: ${fontFam};
  font-size: ${(props) => props.size};
  font-weight: ${(props) => props.fontWeight};
  color: ${(props) => props.color};
`;

export default BlockText;
